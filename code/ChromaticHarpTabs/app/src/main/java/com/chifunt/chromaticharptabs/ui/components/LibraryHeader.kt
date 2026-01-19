package com.chifunt.chromaticharptabs.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.chifunt.chromaticharptabs.R

@Composable
fun LibraryHeader() {
    Text(
        text = stringResource(R.string.library_title),
        fontSize = dimensionResource(R.dimen.headline).value.sp,
        fontWeight = FontWeight.SemiBold
    )
}
