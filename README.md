# ExpenseManager

Action Items:
-------------
Change all Hard Coded String values to R.string
Implement Logs.log in the replacement to System.out
Page is not refreshing after db update. This requires user to select that menu
get Dynamic Currency value.
All Card classes uses log() as toast. Need to change this code with actual log class
In all card classes , the undo bar padding need to be fix. The expected behaviour is, it should be 0 padding
Change popup from HaloBlack  to halo.white 
ListRadio index is in appropriate for the next pager items(List Adapter class)
hard coded value is SMSListener
Notification icon should be in white color


adapter
-------
CategoryCard
ExpenseCard
NotificationCard
Group
MyExpandableListAdapter
ListAdaptersForRadioButton
ListAdaptersForRadioModel

db (this needs to be split in to master,category,budget,dateoperations,cashvault,trends(year progress)
--
DBHealper
Master(proposed)
Category(proposed)
Budget(Proposed)
DBDate(proposed)

Service
-------
SMSListener

Utils
-----
CrossFader(Not In Use)
TYPES
Numbpad
SystemUIHider(need to check)
SystemUIHiderBase(need to check)
SystemUIHiderHoneyComb(need to check)

Form Activities:
----------------
Categories
Expense_add_window
FragmentHistory
Home (Not in Use)
main
PendingAproval
MainActivity
NavigationDrawerAdapter
NavigationDrawerCallBack
NavigationDrawerFragment
NavigationItem

SMSParser
---------
HDFC
SMS


Layouts: (need to find why do we have so much Layouts)
--------
activity_expense_add_window
activity_home
activity_main
activity_main_blacktoolbar
activity_main_topdrawer
activity_nav
calender
card_table_header
card_table_notification
card_table_summary
dialog_category(not in use)
drawer_row
fragment_categories
fragment_fragment_history
fragment_main
fragment_nav
fragment_navigation_drawer
fragment_pending_approval
list_radio_items
list_row_details
list_row_groups
numb_pad
toolbar_default
