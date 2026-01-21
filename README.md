# CCL3_WS2025

GitHub Pages site:
https://chifunt.github.io/ccl3_ws2025/

## Structure

- `pages/` contains the static site content published via GitLab Pages.
- `code/ChromaticHarpTabs/` contains the Android app.

## ChromaticHarpTabs UI layout

The UI is organized by feature and shared components so screens stay small and merge-friendly.

- `ui/screens/` - top-level screens only (composition + wiring).
- `ui/components/common/` - shared primitives (buttons, text fields, chips, top bar).
- `ui/components/filters/` - filter dropdowns and filter option helpers.
- `ui/components/notation/` - notation rendering/editing primitives.
- `ui/components/library/` - library-specific rows, headers, and lists.
- `ui/components/detail/` - tab detail sections and dialogs.
- `ui/components/editor/` - editor cards and picker dialog.
- `ui/components/settings/` - settings screen rows and header.
- `ui/components/cards/` - reusable card UIs (e.g., tab card, metadata pill).
