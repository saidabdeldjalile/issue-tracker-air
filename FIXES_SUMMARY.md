# Chatbot Service Bug Fixes Summary

## Issues Fixed

### 1. chatbot-service/app.py - Indentation Errors

**Location:** Line 182  
**Problem:** Incorrect indentation in FAQ search result handling
```python
# BEFORE (incorrect):
    if faq_result.get('found'):
            response_text = f"Résultat trouvé..."

# AFTER (fixed):
    if faq_result.get('found'):
        response_text = f"Résultat trouvé..."
```

**Location:** Lines 244-254  
**Problem:** Extra indentation in ticket tracking status display
```python
# BEFORE (incorrect):
        if response.status_code == 200:
            ticket = response.json()
            status = ticket.get('status', 'Unknown')
            
             response_text = f"Ticket #{ticket_id} - Statut: {status}\n"  # 13 spaces!
             
             if status in ['Done', 'Closed']:  # 13 spaces!
                 response_text += ...
                 return jsonify({  # 17 spaces!
                     ...
                 })

# AFTER (fixed):
        if response.status_code == 200:
            ticket = response.json()
            status = ticket.get('status', 'Unknown')
            
            response_text = f"Ticket #{ticket_id} - Statut: {status}\n"
            
            if status in ['Done', 'Closed']:
                response_text += ...
                return jsonify({
                    ...
                })
```

**Impact:** These syntax errors prevented the chatbot-service from starting.

---

### 2. ai-service/actions_server.py - Variable Self-Reference

**Location:** Line 17  
**Problem:** `TICKET_API` variable referencing itself before being defined
```python
# BEFORE (incorrect):
BACKEND_URL = "http://localhost:6969/api"
FAQ_API = f"{BACKEND_URL}/faqs"
TICKET_API = f"{TICKET_API}/tickets"  # ERROR: TICKET_API not defined yet!

# AFTER (fixed):
BACKEND_URL = "http://localhost:6969/api"
FAQ_API = f"{BACKEND_URL}/faqs"
TICKET_API = f"{BACKEND_URL}/tickets"
```

**Impact:** This would cause a `NameError` when trying to create tickets via the Rasa actions server.

---

## Files Modified

1. `chatbot-service/app.py` - Fixed 3 indentation errors
2. `ai-service/actions_server.py` - Fixed 1 self-reference bug
3. `ai-service/app.py` - No changes (already correct)
4. `ai-service/actions.py` - No changes (already correct)

## Verification

All Python files now pass syntax validation:
```bash
python -c "import ast; ast.parse(open('chatbot-service/app.py').read()); print('OK')"
python -c "import ast; ast.parse(open('ai-service/actions_server.py').read()); print('OK')"
```

## Architecture Notes

### Production Service Stack
- **Main AI Service** (ai-service/app.py): Uses Rasa NLU + Ollama LLM with proper feedback flow
  - Runs on port 5001
  - Provides `/chat` and `/feedback` endpoints
  - Correctly handles conversation flow with user feedback
  
- **Legacy Chatbot** (chatbot-service/app.py): Simple Flask service
  - Direct ticket creation (bypasses feedback flow)
  - Contains a `/ticket/<id>/track` endpoint
  - Likely not used in production (Docker deploy uses ai-service)

- **Rasa Actions** (ai-service/actions_server.py): Custom Rasa actions
  - Runs on port 5055
  - Handles ticket creation, FAQ search, and classification

### Key Difference
- **ai-service** properly separates concerns: AI provides suggestions → User confirms → Backend creates ticket via `/create_ticket`
- **chatbot-service** directly creates tickets (violates no-direct-creation rule)

## Recommendation

The chatbot-service should either:
1. Be removed/decommissioned (if not in production)
2. Or be refactored to use the same feedback flow as ai-service

Currently, the Docker Compose deploys `ai-chatbot` which uses `ai-service/app.py` (correct implementation), so the chatbot-service bugs are in a legacy/deprecated component.
