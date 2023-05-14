package com.widgetly.iartbook.model

import com.widgetly.iartbookid.command.Command

data class CommandManagerModel(val initialCommand: Command, val commands: MutableList<Command>)
