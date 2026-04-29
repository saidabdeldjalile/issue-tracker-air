# Design Fix for All Frontend Components

## Plan Steps

### Phase 1: Critical Fixes
- [ ] 1. Fix index.css — Remove ALL duplicate CSS rules, consolidate into clean file
- [ ] 2. Fix Home.tsx — Create proper landing page with stats, quick actions, recent activity
- [ ] 3. Fix layout.tsx — Change mt-20 to pt-20 for better navbar spacing

### Phase 2: Core Components
- [ ] 4. Fix notifications.tsx — Convert table to modern card layout, fix hardcoded text
- [ ] 5. Fix ticketlist.tsx — Fix responsive table, hardcoded French text
- [ ] 6. Fix projectlist.tsx — Design polish, consistency
- [ ] 7. Fix loginform.tsx — Remove inline styles, responsive improvements
- [ ] 8. Fix navbar.tsx — Verify drawer/z-index fixes

### Phase 3: Ticket Pages
- [ ] 9. Fix ticketdetails.tsx — Remove duplicate TicketActions, fix layout
- [ ] 10. Fix ticketbody.tsx — Use base-* classes instead of gray-*
- [ ] 11. Fix ticket.tsx — Consistency

### Phase 4: Other Components
- [ ] 12. Fix departmentlist.tsx — Remove min-h-screen wrapper, consistency
- [ ] 13. Fix knowledgecenter.tsx — Remove min-h-screen wrapper, fix duplicate search
- [ ] 14. Fix sla.tsx — Fix hardcoded text, stat styling
- [ ] 15. Fix userlist.tsx — Polish design
- [ ] 16. Fix settings.tsx — Polish design
- [ ] 17. Fix registrationform.tsx — Use base-* classes
- [ ] 18. Fix comment.tsx — Fix hardcoded French text
- [ ] 19. Fix alltickets.tsx — Fix undefined variable

### Phase 5: Testing
- [ ] 20. Test all pages with npm run dev
- [ ] 21. Verify responsive design
- [ ] 22. Verify dark/light theme

## Dependent Files
- index.css (affects all components)
- layout.tsx (affects all pages)
- Home.tsx (landing page)

## Followup Steps
1. cd issue-tracker-web && npm run dev
2. Test responsive design on mobile/tablet/desktop
3. Verify dark/light theme switching
