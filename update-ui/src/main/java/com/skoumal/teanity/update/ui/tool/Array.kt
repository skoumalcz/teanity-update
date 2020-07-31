package com.skoumal.teanity.update.ui.tool

internal var FloatArray.topLeft
    inline get() = this[0]
    inline set(value) {
        this[0] = value
    }

internal var FloatArray.topRight
    inline get() = this[1]
    inline set(value) {
        this[1] = value
    }

internal var FloatArray.bottomRight
    inline get() = this[2]
    inline set(value) {
        this[2] = value
    }

internal var FloatArray.bottomLeft
    inline get() = this[3]
    inline set(value) {
        this[3] = value
    }