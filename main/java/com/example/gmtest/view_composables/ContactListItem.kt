package com.example.gmtest.view_composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gmtest.models.FlowModel

@Composable
fun ContactListItem(
    id: Long,
    name: String,
    thumbnail: String?,
    phone: String?,
    onItemClick: (String) -> Unit,
    flowModel: FlowModel = FlowModel()
) {
    Box(
        modifier = Modifier
            .heightIn(min = 70.dp)
            .clickable(onClick = { onItemClick(id.toString()) })
            .background(color = Color.White, shape = RoundedCornerShape(2.dp))
            .padding(all = 2.dp)
    ) {
        Row {
            Box(modifier = Modifier
                .padding(start=10.dp)
                .size(50.dp)
                .aspectRatio(1f)
                .background(Color.Green, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (thumbnail == null) {
                    Text(
                        text = name[0].toString(),
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier
                        //.padding(start = 10.dp)
                        //.background(Color.Green, CircleShape)
                        //.badgeLayout()
                    )

                } else {
                    Image(
                        bitmap = flowModel.getBitmap(thumbnail, LocalContext.current)!!,
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)


                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(start = 10.dp, end = 40.dp)
            ) {
                Text(
                    text = name,
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                if( phone!=null)
                    Text(
                        text = phone, color = Color.Black, fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ContactListItemPreview() {
    ContactListItem(1, name = "Boris", thumbnail = null, phone = "0532583678", onItemClick = {})
}