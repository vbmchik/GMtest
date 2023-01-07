package com.example.gmtest.view_composables


import android.annotation.SuppressLint
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gmtest.models.ContactModel
import com.example.gmtest.models.UpdateModel


@Composable
fun <T : Any> rememberMutableStateListOf(vararg elements: T): SnapshotStateList<T> {
    return rememberSaveable(
        saver = listSaver(
            save = { stateList ->
                if (stateList.isNotEmpty()) {
                    val first = stateList.first()
                    if (!canBeSaved(first)) {
                        throw IllegalStateException("${first::class} cannot be saved. By default only types which can be stored in the Bundle class can be saved.")
                    }
                }
                stateList.toList()
            },
            restore = { it.toMutableStateList() }
        )
    ) {
        elements.toList().toMutableStateList()
    }
}




@SuppressLint("ModifierFactoryExtensionFunction")
fun orientationModifier(flag: Boolean): Modifier
{
        if (!flag)
            return Modifier
                .size(400.dp)
                .heightIn(max = 200.dp)
                .background(Color.Transparent)
        else
            return Modifier
                .size(400.dp)
                .heightIn(max = 200.dp)
                .background(Color.Blue)

}

@Composable
fun PictureBox(updateModel: UpdateModel = UpdateModel(), person: ContactModel) {

    val image = updateModel.getPhotoFromUri(person.photoURI, LocalContext.current)
    Box(
        orientationModifier(image==null),
        contentAlignment = Alignment.Center
    ) {

        if (image == null) {
            Text(
                text = person.name!![0].toString(),
                fontSize = 200.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

        } else {
            Image(
                bitmap = image,
                contentDescription = "",
                alignment = Alignment.Center,
                modifier = Modifier
                    .size(400.dp)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun Details(
    valueStateList: SnapshotStateList<String>,
    person: ContactModel,
    updateModel: UpdateModel = UpdateModel()
) {
    Divider(modifier = Modifier.padding(10.dp))
    val res = LocalContext.current.contentResolver
    var t = 1
    val lo = LocalContext.current
    TextField(
        value = valueStateList[0],
        onValueChange = {
            valueStateList[0] = it
        },
        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
        singleLine = true,
        modifier = Modifier.fillMaxWidth().background(Color.DarkGray)
    )
    for (phone in person.mobileNumber) {
        val p = t
        TextField(
            value = valueStateList[p],
            onValueChange = {
                valueStateList[p] = it
            },
            textStyle = TextStyle(color = Color.Green, fontSize = 18.sp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().background(Color.DarkGray)
        )
        t++
    }
    for (email in person.email) {
        val p = t
        TextField(
            value = valueStateList[p],
            onValueChange = {
                valueStateList[p] = it
            },
            textStyle = TextStyle(color = Color.Red, fontSize = 18.sp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().background(Color.DarkGray)
        )
        t++
    }
    Divider(modifier = Modifier.padding(10.dp))
    val c = LocalContext.current
    Button(onClick = {
        updateModel.editContact(lo, ctx = res, contact = person, state = valueStateList)
        Toast.makeText(c,"Saved!",Toast.LENGTH_SHORT).show()
    }) {
        Text(text = "Save changes!")
    }
}

@Composable
fun DetailsScreen(person: ContactModel, updateModel: UpdateModel = UpdateModel()) {

    val valueStateList = rememberMutableStateListOf<String>()

    if (valueStateList.size == 0) {
        valueStateList.add(person.name.toString())
        valueStateList.addAll(person.mobileNumber)
        valueStateList.addAll(person.email)
    }


    when (LocalConfiguration.current.orientation) {
        ORIENTATION_LANDSCAPE -> {
            Row(
                modifier = Modifier
                    //.fillMaxSize()
                    .background(Color.Transparent)
                //.wrapContentSize(Alignment.TopCenter)

            ) {
                PictureBox(updateModel, person)
                Column(
                    modifier = Modifier
                        //.fillMaxSize()
                        .background(Color.DarkGray)
                        //.wrapContentSize(Alignment.TopCenter)
                        .verticalScroll(rememberScrollState())
                ) {
                    Details(valueStateList, person, updateModel)
                }
            }
        }
        else -> {
            Column(
                modifier = Modifier
                    //.fillMaxSize()
                    .background(Color.Transparent)
                    //.wrapContentSize(Alignment.TopCenter)
                    .verticalScroll(rememberScrollState())
            ) {
                PictureBox(updateModel, person)
                Details(valueStateList, person, updateModel)
            }
        }
    }


}


@Preview(showBackground = true)
@Composable
fun DetailsScreenPreview() {
    DetailsScreen(ContactModel().also { it.name = "Phil" })
}
