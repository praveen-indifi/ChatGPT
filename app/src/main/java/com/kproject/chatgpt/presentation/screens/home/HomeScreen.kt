package com.kproject.chatgpt.presentation.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kproject.chatgpt.R
import com.kproject.chatgpt.presentation.model.*
import com.kproject.chatgpt.presentation.screens.components.*
import com.kproject.chatgpt.presentation.screens.home.components.ApiKeyAlertDialog
import com.kproject.chatgpt.presentation.screens.home.components.ChatOptionsAlertDialog
import com.kproject.chatgpt.presentation.screens.home.components.ModeSelectionAlertDialog
import com.kproject.chatgpt.presentation.screens.home.components.ThemeOptionAlertDialog
import com.kproject.chatgpt.presentation.screens.utils.Utils
import com.kproject.chatgpt.presentation.theme.CompletePreview
import com.kproject.chatgpt.presentation.theme.PreviewTheme

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onNavigateToChatScreen: (chatArgs: ChatArgs) -> Unit,
) {
    val uiState = homeViewModel.homeUiState
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var showThemeOptionDialog by remember { mutableStateOf(false) }

    HomeScreenContent(
        homeUiState = uiState,
        onChangeApiKey = { showApiKeyDialog = true },
        onChangeAppTheme = { showThemeOptionDialog = true },
        onStartNewChat = { chatName, conversationMode ->
            val chatArgs = ChatArgs(
                chatId = UnspecifiedChatId,
                chatName = chatName,
                isChatMode = (conversationMode == ConversationMode.ChatMode)
            )
            onNavigateToChatScreen.invoke(chatArgs)
        },
        onChatSelected = { recentChat ->
            val chatArgs = ChatArgs(
                chatId = recentChat.chatId,
                chatName = UnspecifiedChatName,
                isChatMode = recentChat.chatMode
            )
            onNavigateToChatScreen.invoke(chatArgs)
        },
        onRenameChat = { newChatName, recentChat ->
            homeViewModel.renameRecentChat(newChatName, recentChat)
        },
        onClearChat = { recentChat ->
            homeViewModel.clearRecentChat(recentChat)
        },
        onDeleteChat = { recentChat ->
            homeViewModel.deleteRecentChat(recentChat)
        }
    )

    ApiKeyAlertDialog(
        showDialog = showApiKeyDialog,
        onDismiss = { showApiKeyDialog = false },
        apiKey = uiState.apiKey,
        onSaveApiKey = { apiKey ->
            homeViewModel.saveApiKey(apiKey)
        }
    )

    ThemeOptionAlertDialog(
        showDialog = showThemeOptionDialog,
        onDismiss = { showThemeOptionDialog = false },
        currentSelectedTheme = uiState.themeOption,
        currentDarkMode = uiState.isDarkMode,
        onThemeSelected = { themeOption, isDarkMode ->
            homeViewModel.changeThemeOption(themeOption, isDarkMode)
        }
    )
}

@Composable
private fun HomeScreenContent(
    homeUiState: HomeUiState,
    onChangeApiKey: () -> Unit,
    onChangeAppTheme: () -> Unit,
    onStartNewChat: (chatName: String, conversationMode: ConversationMode) -> Unit,
    onChatSelected: (recentChat: RecentChat) -> Unit,
    onRenameChat: (newTitle: String, recentChat: RecentChat) -> Unit,
    onClearChat: (recentChat: RecentChat) -> Unit,
    onDeleteChat: (recentChat: RecentChat) -> Unit,
) {
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showNewChatDialog by remember { mutableStateOf(false) }
    var showChatOptionsDialog by remember { mutableStateOf(false) }
    var selectedRecentChat by remember { mutableStateOf(RecentChat()) }

    var showRenameChatDialog by remember { mutableStateOf(false) }
    var showClearChatDialog by remember { mutableStateOf(false) }
    var showDeleteChatDialog by remember { mutableStateOf(false) }

    var chatName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(id = R.string.app_name),
                actions = {
                    IconButton(onClick = { showOptionsMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onSurface
                        )
                    }

                    OptionsDropdownMenu(
                        showOptionsMenu = showOptionsMenu,
                        onDismiss = { showOptionsMenu = false },
                        onApiKeyOptionClick = onChangeApiKey,
                        onAppThemeOptionClick = onChangeAppTheme
                    )
                }
            )
        },
        floatingActionButton = {
            if (homeUiState.apiKey.isNotBlank()) {
                FloatingActionButton(onClick = { showNewChatDialog = true }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_chat),
                        null,
                        tint = MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    ) { paddingValues ->
        Content(
            modifier = Modifier.padding(paddingValues),
            homeUiState = homeUiState,
            onChatSelected = { recentChat ->
                onChatSelected.invoke(recentChat)
            },
            onShowChatOptions = { recentChat ->
                selectedRecentChat = recentChat
                chatName = recentChat.chatName
                showChatOptionsDialog = true
            },
            onInsertApiKey = onChangeApiKey
        )

        ChatOptionsAlertDialog(
            showDialog = showChatOptionsDialog,
            onDismiss = { showChatOptionsDialog = false },
            onRenameChatOptionClick = { showRenameChatDialog = true },
            onClearChatOptionClick = { showClearChatDialog = true },
            onDeleteChatOptionClick = { showDeleteChatDialog = true }
        )

        NewChatAlertDialog(
            showDialog = showNewChatDialog,
            onDismiss = { showNewChatDialog = false },
            onStartNewChat = { chatName, conversationMode ->
                onStartNewChat.invoke(chatName, conversationMode)
            }
        )

        // Rename Chat Dialog
        TextFieldAlertDialog(
            showDialog = showRenameChatDialog,
            onDismiss = { showRenameChatDialog = false },
            title = stringResource(id = R.string.rename_chat),
            textFieldValue = chatName,
            textFieldPlaceholder = stringResource(id = R.string.insert_chat_name),
            okButtonEnabled = chatName.isNotBlank(),
            okButtonTitle = stringResource(id = R.string.button_save),
            onTextValueChange = {
                chatName = it
            },
            onClickButtonOk = {
                onRenameChat.invoke(chatName, selectedRecentChat)
            }
        )

        // Clear Chat Dialog
        SimpleAlertDialog(
            showDialog = showClearChatDialog,
            onDismiss = { showClearChatDialog = false },
            title = stringResource(id = R.string.clear_chat),
            message = stringResource(id = R.string.clear_chat_confirmation),
            onClickButtonOk = {
                onClearChat.invoke(selectedRecentChat)
            }
        )

        // Delete Chat Dialog
        SimpleAlertDialog(
            showDialog = showDeleteChatDialog,
            onDismiss = { showDeleteChatDialog = false },
            title = stringResource(id = R.string.delete_chat),
            message = stringResource(id = R.string.delete_chat_confirmation),
            onClickButtonOk = {
                onDeleteChat.invoke(selectedRecentChat)
            }
        )
    }
}

@Composable
private fun OptionsDropdownMenu(
    showOptionsMenu: Boolean,
    onDismiss: () -> Unit,
    onApiKeyOptionClick: () -> Unit,
    onAppThemeOptionClick: () -> Unit
) {
    val context = LocalContext.current
    DropdownMenu(
        expanded = showOptionsMenu,
        onDismissRequest = onDismiss,
        modifier = Modifier.background(MaterialTheme.colors.surface)
    ) {
        DropdownMenuItem(
            onClick = {
                onDismiss.invoke()
                onApiKeyOptionClick.invoke()
            }
        ) {
            Text(
                text = stringResource(id = R.string.api_key),
                color = MaterialTheme.colors.onSurface
            )
        }

        DropdownMenuItem(
            onClick = {
                onDismiss.invoke()
                onAppThemeOptionClick.invoke()
            }
        ) {
            Text(
                text = stringResource(id = R.string.app_theme),
                color = MaterialTheme.colors.onSurface
            )
        }

        DropdownMenuItem(
            onClick = {
                onDismiss.invoke()
                val url = "https://beta.openai.com/account/usage"
                Utils.openUrl(context, url)
            }
        ) {
            Text(
                text = stringResource(id = R.string.api_consumption),
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    homeUiState: HomeUiState,
    onChatSelected: (recentChat: RecentChat) -> Unit,
    onShowChatOptions: (recentChat: RecentChat) -> Unit,
    onInsertApiKey: () -> Unit
) {
    if (homeUiState.isLoading) {
        ProgressIndicator()
    } else {
        if (homeUiState.apiKey.isNotBlank()) {
            RecentChatsList(
                recentChatsList = homeUiState.recentChatsList,
                onClick = { recentChat ->
                    onChatSelected.invoke(recentChat)
                },
                onLongClick = { recentChat ->
                    onShowChatOptions.invoke(recentChat)
                },
                modifier = modifier,
            )
        } else {
            EmptyApiKeyInfo(
                onInsertApiKey = onInsertApiKey
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
private fun RecentChatsList(
    modifier: Modifier = Modifier,
    recentChatsList: List<RecentChat>,
    onClick: (recentChat: RecentChat) -> Unit,
    onLongClick: (recentChat: RecentChat) -> Unit
) {
    AnimatedContent(targetState = recentChatsList.isNotEmpty()) { recentChatsListIsNotEmpty ->
        if (recentChatsListIsNotEmpty) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
            ) {
                items(
                    items = recentChatsList,
                    key = { recentChat -> recentChat.chatId }
                ) { recentChat ->
                    RecentChatsListItem(
                        recentChat = recentChat,
                        onClick = {
                            onClick(recentChat)
                        },
                        onLongClick = {
                            onLongClick.invoke(recentChat)
                        },
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = tween(durationMillis = 600)
                        )
                    )
                }
            }
        } else {
            EmptyListInfo(
                iconResId = R.drawable.ic_chat,
                title = stringResource(id = R.string.info_title_empty_recent_chats_list),
                description = stringResource(id = R.string.info_description_empty_recent_chats_list)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RecentChatsListItem(
    modifier: Modifier = Modifier,
    recentChat: RecentChat,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    // Prevents multiple calls to onClick, avoiding opening ChatScreen multiple times
    var notClicked by remember { mutableStateOf(true) }
    Column(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    if (notClicked) {
                        onClick.invoke()
                        notClicked = false
                    }
                },
                onLongClick = onLongClick,
            )
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val chatIcon = if (recentChat.chatMode) {
                R.drawable.ic_chat
            } else {
                R.drawable.ic_manage_search
            }
            Image(
                imageVector = ImageVector.vectorResource(id = chatIcon),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .fillMaxSize()
                    .background(MaterialTheme.colors.surface),
            )

            Spacer(Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = recentChat.chatName,
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (recentChat.lastMessage.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = recentChat.lastMessage,
                        color = MaterialTheme.colors.onPrimary.copy(0.6f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = recentChat.formattedDate,
                color = MaterialTheme.colors.onPrimary,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

@Composable
private fun NewChatAlertDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onStartNewChat: (chatName: String, conversationMode: ConversationMode) -> Unit
) {
    var showModeSelectionDialog by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf("") }
    var chatName by remember { mutableStateOf("") }

    TextFieldAlertDialog(
        showDialog = showDialog,
        onDismiss = {
            textFieldValue = ""
            onDismiss.invoke()
        },
        title = stringResource(id = R.string.chat_name),
        textFieldValue = textFieldValue,
        textFieldPlaceholder = stringResource(id = R.string.insert_chat_name),
        okButtonEnabled = textFieldValue.isNotBlank(),
        okButtonTitle = stringResource(id = R.string.button_continue),
        onTextValueChange = {
            textFieldValue = it
            chatName = it
        },
        onClickButtonOk = { showModeSelectionDialog = true }
    )

    ModeSelectionAlertDialog(
        showDialog = showModeSelectionDialog,
        onDismiss = { showModeSelectionDialog = false },
        onModeSelected = { conversationMode ->
            onStartNewChat.invoke(chatName, conversationMode)
        }
    )
}

@Composable
private fun EmptyApiKeyInfo(
    modifier: Modifier = Modifier,
    onInsertApiKey: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_key_off),
            contentDescription = null,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(110.dp)
        )
        Text(
            text = stringResource(id = R.string.info_title_empty_api_key),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(all = 6.dp)
        )
        Text(
            text = stringResource(id = R.string.info_description_empty_api_key),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onPrimary,
            fontSize = 16.sp,
            modifier = Modifier.padding(all = 6.dp)
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                val url = "https://beta.openai.com/account/api-keys"
                Utils.openUrl(context, url)
            },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.access_url),
                fontSize = 16.sp,
                color = MaterialTheme.colors.onSurface
            )
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onInsertApiKey,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = MaterialTheme.colors.background,
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colors.secondary.copy(0.5f)
            )
        ) {
            Text(
                text = stringResource(id = R.string.i_have_the_api_key),
                fontSize = 16.sp,
                color = MaterialTheme.colors.secondary
            )
        }
    }
}

@CompletePreview
@Composable
private fun Preview() {
    PreviewTheme {
        val uiState = HomeUiState(
            isLoading = false,
            recentChatsList = fakeRecentChatsList,
            apiKey = "123"
        )
        HomeScreenContent(
            homeUiState = uiState,
            onChangeApiKey = {},
            onChangeAppTheme = {},
            onStartNewChat = { _, _ -> },
            onChatSelected = {},
            onRenameChat = { _, _ -> },
            onClearChat = {},
            onDeleteChat = {}
        )
    }
}