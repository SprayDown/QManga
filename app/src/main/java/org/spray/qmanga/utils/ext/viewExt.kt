package org.spray.qmanga.utils.ext

import androidx.appcompat.widget.Toolbar

fun Toolbar.forceShowBar() {
    clearAnimation();
    animate().translationY(0F).duration = 200;
}