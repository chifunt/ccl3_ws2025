package com.chifunt.chromaticharptabs.ui.navigation

const val NAV_ARG_TAB_ID = "tabId"
const val ROUTE_LIBRARY = "library"
const val ROUTE_DETAIL_BASE = "detail"
const val ROUTE_DETAIL = "$ROUTE_DETAIL_BASE/{$NAV_ARG_TAB_ID}"
const val ROUTE_EDITOR_BASE = "editor"
const val ROUTE_EDITOR = "$ROUTE_EDITOR_BASE?$NAV_ARG_TAB_ID={$NAV_ARG_TAB_ID}"
const val ROUTE_PRACTICE_BASE = "practice"
const val ROUTE_PRACTICE = "$ROUTE_PRACTICE_BASE/{$NAV_ARG_TAB_ID}"

fun detailRoute(tabId: Int): String = "$ROUTE_DETAIL_BASE/$tabId"

fun editorRoute(tabId: Int?): String {
    return if (tabId == null) {
        ROUTE_EDITOR_BASE
    } else {
        "$ROUTE_EDITOR_BASE?$NAV_ARG_TAB_ID=$tabId"
    }
}

fun practiceRoute(tabId: Int): String = "$ROUTE_PRACTICE_BASE/$tabId"
