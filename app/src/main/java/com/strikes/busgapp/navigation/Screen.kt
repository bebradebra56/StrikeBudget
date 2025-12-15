package com.strikes.busgapp.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object Activity : Screen("activity")
    object Limits : Screen("limits")
    object Settings : Screen("settings")
    object StrikeAdd : Screen("strike_add")
    object OperationDetails : Screen("operation_details/{transactionId}") {
        fun createRoute(transactionId: Long) = "operation_details/$transactionId"
    }
    object CreateLimit : Screen("create_limit?limitId={limitId}") {
        fun createRoute(limitId: Long? = null) = if (limitId != null) "create_limit?limitId=$limitId" else "create_limit"
    }
    object Templates : Screen("templates")
    object CreateTemplate : Screen("create_template?templateId={templateId}") {
        fun createRoute(templateId: Long? = null) = if (templateId != null) "create_template?templateId=$templateId" else "create_template"
    }
    object Recurring : Screen("recurring")
    object CreateRecurring : Screen("create_recurring?recurringId={recurringId}") {
        fun createRoute(recurringId: Long? = null) = if (recurringId != null) "create_recurring?recurringId=$recurringId" else "create_recurring"
    }
    object Insights : Screen("insights")
    object Wallets : Screen("wallets")
    object Export : Screen("export")
}

