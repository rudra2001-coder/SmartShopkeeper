
package com.rudra.smartshopkeeper.navigation

sealed class Screen(val route: String) {
    object DashboardScreen : Screen("dashboard_screen")
    object NewSaleScreen : Screen("new_sale_screen")
    object ProductListScreen : Screen("product_list_screen")
    object AddEditProductScreen : Screen("add_edit_product_screen")
    object CustomerListScreen : Screen("customer_list_screen")
    object AddEditCustomerScreen : Screen("add_edit_customer_screen")
    object CustomerLedgerScreen : Screen("customer_ledger_screen")
    object SupplierListScreen : Screen("supplier_list_screen")
    object AddEditSupplierScreen : Screen("add_edit_supplier_screen")
    object ExpenseListScreen : Screen("expense_list_screen")
    object AddEditExpenseScreen : Screen("add_edit_expense_screen")
    object AddPurchaseScreen : Screen("add_purchase_screen")
    object SupplierLedgerScreen : Screen("supplier_ledger_screen")
    object ReportScreen : Screen("report_screen")
    object SaleReturnScreen : Screen("sale_return_screen")
    object CustomerPurchaseHistoryScreen : Screen("customer_purchase_history_screen")
    object ExpenseReportScreen : Screen("expense_report_screen")
    object ExpenseVsIncomeScreen : Screen("expense_vs_income_screen")
    object ProfitPerProductScreen : Screen("profit_per_product_screen")
    object ProfitPerCategoryScreen : Screen("profit_per_category_screen")
}
