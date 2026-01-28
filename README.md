# CCL3_WS2025 — Chromatic HarpTabs

Repository for the Chromatic HarpTabs Android app and its project documentation site.

Live site (GitHub Pages):
https://chifunt.github.io/ccl3_ws2025/

## Project layout

- `code/ChromaticHarpTabs/` — Android app source (Kotlin + Jetpack Compose).
- `pages/` — Static documentation site (HTML/CSS/JS), deployed via GitHub Pages.

## UI structure (Compose)

The UI is organized by feature and shared components so screens stay small and merge‑friendly.

- `ui/screens/` — top‑level screens only (composition + wiring).
- `ui/components/common/` — shared primitives (buttons, text fields, chips, top bar).
- `ui/components/filters/` — filter dropdowns and filter option helpers.
- `ui/components/notation/` — notation rendering/editing primitives.
- `ui/components/library/` — library-specific rows, headers, and lists.
- `ui/components/detail/` — tab detail sections and dialogs.
- `ui/components/editor/` — editor cards and picker dialog.
- `ui/components/settings/` — settings screen rows and header.
- `ui/components/cards/` — reusable card UIs (e.g., tab card, metadata pill).
- `ui/components/practice/` — practice mode UI components.
- `ui/components/virtualharmonica/` — virtual harmonica UI components.

## Data layer

- `data/model/` — core models and notation data structures.
- `data/notation/` — chromatic harmonica note mapping utilities.
- `data/util/` — tag parsing/normalization helpers.
- `data/repository/` — repository layer wrapping Room access.
- `db/` — Room entities, DAO, and database setup.

## Contributing

Open a PR with a short summary and screenshots (if UI changes).
