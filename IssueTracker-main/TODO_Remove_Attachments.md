# TODO: Remove Attachments/Screenshots Concept - Progress Tracking

## Plan Overview
Remove all attachment/screenshot functionality from backend, frontend, and DB.

## Steps (2/7 - Phase 1 cleanup files deleted)

### ✅ Phase 1: Delete Core Files [COMPLETE]
- ✅ src/main/java/com/suryakn/IssueTracker/entity/Attachment.java
- ✅ src/main/java/com/suryakn/IssueTracker/dto/AttachmentDto.java
- ✅ src/main/java/com/suryakn/IssueTracker/repository/AttachmentRepository.java
- ✅ src/main/java/com/suryakn/IssueTracker/service/AttachmentService.java
- ✅ src/main/java/com/suryakn/IssueTracker/controller/AttachmentController.java
- ✅ issue-tracker-web/src/components/ImageModal.tsx
- ✅ delete_all_attachments.sql, add_attachments_table.sql, run_delete_attachments.bat
- ✅ TODO_Attachments.md, TODO_Fix_Screenshot_Upload.md, TODO_ScreenshotModal.md

### ✅ Phase 2: Clean DTOs [COMPLETE]
- ✅ src/main/java/com/suryakn/IssueTracker/dto/TicketResponse.java (remove attachments)
- ✅ src/main/java/com/suryakn/IssueTracker/dto/CommentDto.java (remove attachments)

### ✅ Phase 3: Clean Entities [COMPLETE]
- ✅ src/main/java/com/suryakn/IssueTracker/entity/Ticket.java (remove attachments)
- ✅ src/main/java/com/suryakn/IssueTracker/entity/Comment.java (remove attachments)

### ✅ Phase 4: Clean Services [COMPLETE]
- ✅ src/main/java/com/suryakn/IssueTracker/service/TicketService.java
- ✅ src/main/java/com/suryakn/IssueTracker/service/CommentService.java

### Phase 5: Frontend Rewrite [TODO]
- [ ] issue-tracker-web/src/createticket.tsx (remove upload/preview)
- [ ] issue-tracker-web/src/components/comment.tsx (remove upload/screenshot/modal)

### ✅ Phase 6: Types & Controllers [COMPLETE]
- ✅ issue-tracker-web/src/TicketResponse.ts


### Phase 7: Final Cleanup & Test [TODO]
- [ ] Update schema files to drop attachment table
- [ ] Test ticket creation/comments

**Current Progress: Phases 1-4 Complete - Backend clean. Frontend next.**

