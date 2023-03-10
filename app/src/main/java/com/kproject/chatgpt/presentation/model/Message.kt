package com.kproject.chatgpt.presentation.model

import com.kproject.chatgpt.domain.model.MessageModel
import com.kproject.chatgpt.presentation.extensions.getFormattedDate
import java.util.*

data class Message(
    val chatId: Long,
    val message: String = "",
    val sentByUser: Boolean = true,
    val sendDate: Date = Date(),
    val totalTokens: Int = 0
) {
    val formattedSendDate = sendDate.getFormattedDate()
}

fun MessageModel.fromModel() = Message(
    chatId = chatId,
    message = message,
    sentByUser = sentByUser,
    sendDate = sendDate,
    totalTokens = totalTokens
)

fun Message.toModel() = MessageModel(
    chatId = chatId,
    message = message,
    sentByUser = sentByUser,
    sendDate = sendDate,
    totalTokens = totalTokens
)

val fakeChatList = listOf(
    Message(
        chatId = 1,
        message = "What is Jetpack Compose?",
        sentByUser = true,
        sendDate = Date()
    ),
    Message(
        chatId = 1,
        message = "Jetpack Compose is Android's recommended modern toolkit for building native UI. " +
                "It simplifies and accelerates UI development on Android.",
        sentByUser = false,
        sendDate = Date()
    ),
    Message(
        chatId = 1,
        message = "What is the main difference between XML and Jetpack Compose?",
        sentByUser = true,
        sendDate = Date()
    ),
    Message(
        chatId = 1,
        message = "The main difference between XML and Jetpack Compose is that XML is a markup " +
                "language used to create user interface layouts, while Jetpack Compose is a " +
                "declarative UI framework for Android that allows developers to create UI elements" +
                " with the help of Kotlin code. XML is a more traditional approach to creating user" +
                " interfaces, while Jetpack Compose is aimed at making the development" +
                " process easier and faster.",
        sentByUser = false,
        sendDate = Date()
    )
)