package com.moontv.application.player

interface OnChangeSubtitleListener {
    fun onChangeSubTitle(enable: Boolean, otherAction: Boolean,pos:Int)
}