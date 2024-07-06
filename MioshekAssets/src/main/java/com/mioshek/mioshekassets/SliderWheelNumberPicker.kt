package com.mioshek.mioshekassets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun rememberPickerState() = remember { PickerState() }

class PickerState {
    var selectedItem by mutableStateOf("")
}

/**
 * Infinite number picker similar to the one that in MIUI clock app.
 * @param wheelValues takes string array and loops its arguments the way it looks infinite
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SliderWheelNumberPicker(
    wheelValues: Array<Array<String>>,
    startIndex: Array<Int>,
    modifier: Modifier = Modifier,
    visibleItemsCount: Int = 3,
    stringSeparator: String = ":",
    textColor: Color = Color.Black,
    showSeparator: Boolean = false,
    onValueChange: (ArrayList<Long>) -> Unit = {},
    padding: Dp = 10.dp,
    fontSize: TextUnit = 60.sp,
    showHandIcon: Boolean = false,
    alignment:  Alignment.Vertical = Alignment.CenterVertically
){
    var pickedValues by remember {
        mutableStateOf(arrayListOf(0L,0L,0L))
    }
    val listScrollCount = Int.MAX_VALUE
    val listScrollMiddle = listScrollCount / 2
    val visibleItemsMiddle = visibleItemsCount /2
    val itemHeightPixels = remember { mutableIntStateOf(0) }
    val itemHeightDp = pixelsToDp(itemHeightPixels.intValue)
    val fadingEdgeGradient = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.3f to textColor.copy(0.2f),
            0.5f to textColor,
            0.7f to textColor.copy(0.2f),
            1f to Color.Transparent
        )
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = alignment
        ) {
            wheelValues.forEachIndexed { tableIndex, column ->
                val listStartIndex = listScrollMiddle - listScrollMiddle % column.size - visibleItemsMiddle + startIndex[tableIndex]
                val listState = rememberLazyListState(initialFirstVisibleItemIndex = listStartIndex)
                val coroutineScope = rememberCoroutineScope()
                val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
                coroutineScope.launch { listState.scrollToItem(listStartIndex) }

                LazyColumn(
                    state = listState,
                    flingBehavior = flingBehavior,
                    modifier = Modifier
                        .height(itemHeightDp * visibleItemsCount)
                        .fadingEdge(fadingEdgeGradient)
                        .padding(start = padding, end = padding)
                ){
                    items(listScrollCount){index ->
                        pickedValues[tableIndex] = (column[((listState.firstVisibleItemIndex + 1) % column.size)]).toLong()
                        onValueChange(pickedValues)
                        val stringNumber = column[index % column.size]
                        Text(
                            text = if(stringNumber.length == 1) "  $stringNumber" else stringNumber,
                            maxLines = 1,
                            fontSize = fontSize,
                            fontFamily = FontFamily.Serif,
                            overflow = TextOverflow.Clip,
                            modifier = Modifier
                                .onSizeChanged { size ->
                                    itemHeightPixels.value = size.height
                                }
                                .then(modifier.padding(top = padding, bottom = padding))
                                .clickable {
                                    coroutineScope.launch {
                                        listState.scrollToItem(index - 1)
                                    }
                                }
                        )
                    }
                }

                if(tableIndex != wheelValues.size -1 && showSeparator){
                    Text(text = stringSeparator, fontSize = fontSize)
                }
            }
        }

    }
}

private fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Composable
private fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }