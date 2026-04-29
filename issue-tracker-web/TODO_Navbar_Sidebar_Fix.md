# TODO: Navbar, Sidebar & Notifications Fix

## Steps

- [ ] 1. Update `layout.tsx` - Add top padding to page-shell for sticky navbar
- [x] 2. Update `navbar.tsx` - Make navbar sticky, add mobile hamburger, add drawer/sidebar, fix dropdown z-index
- [x] 3. Update `index.css` - Add sticky navbar styles, fix dropdown z-index
- [x] 4. Update `NotificationBell.tsx` - Fix notification dropdown positioning
- [ ] 5. Test all changes

## Details

### Step 1: layout.tsx
- Add `pt-20` (or equivalent) to page-shell to prevent content overlap with sticky navbar

### Step 2: navbar.tsx
- Add `sticky top-0 z-40` to navbar container
- Add hamburger menu button for mobile (`lg:hidden`)
- Add DaisyUI drawer structure for mobile navigation
- Ensure user dropdown has proper z-index (`z-[60]`)
- Ensure notification dropdown has proper z-index

### Step 3: index.css
- Ensure `.navbar` works properly with sticky positioning
- Add `z-index: 60` to `.dropdown-content`
- Add drawer styles if needed

### Step 4: NotificationBell.tsx
- Ensure dropdown has `z-[60]` class
- Verify positioning is correct
