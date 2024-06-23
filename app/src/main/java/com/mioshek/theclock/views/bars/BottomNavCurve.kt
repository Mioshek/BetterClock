package com.mioshek.theclock.views.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.mioshek.theclock.data.Storage

class BottomNavCurve : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val padding = 20f
        var cornerRadius = CornerRadius(padding * 5, padding * 5)
        return Outline.Generic(path = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(
                        offset = Offset(padding, 0f),
                        size = Size(size.width - padding * 2, size.height - padding),
                    ),
                    topLeft = cornerRadius,
                    topRight = cornerRadius,
                    bottomLeft = cornerRadius,
                    bottomRight = cornerRadius,
                )
            )

//            val selectedIndex = try {
//                Storage.take<Int>("BottomNavBarItemIndex")
//            }
//            catch (e: Exception){
//                2
//            }
//            val offset = selectedIndex - 1
//            val biggerCircleRadius = size.width / 12
//            val smallerCircleRadius = size.width / 24
//            val additional = (size.width / 3) * offset + (smallerCircleRadius /5) * offset
//            addArc(
//                oval = Rect(
//                    offset = Offset(size.width/5 - biggerCircleRadius + additional, - biggerCircleRadius/2),
//                    size = Size(biggerCircleRadius, biggerCircleRadius)
//                ),
//                startAngleDegrees = 0f,
//                sweepAngleDegrees = 360f
//            )
//
//            addArc(
//                oval = Rect(
//                    offset = Offset(size.width/2 - biggerCircleRadius, - biggerCircleRadius/2),
//                    size = Size(smallerCircleRadius, smallerCircleRadius)
//                ),
//                startAngleDegrees = 0f,
//                sweepAngleDegrees = 360f
//            )

            close()
        }
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ShapePreview(){
    Box(modifier = Modifier.fillMaxSize().clip(BottomNavCurve()).background(Color.Green))
}