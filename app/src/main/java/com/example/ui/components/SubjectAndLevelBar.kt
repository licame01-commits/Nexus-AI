package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChatGptBackground
import com.example.ui.theme.ChatGptChipBg
import com.example.ui.theme.ChatGptChipBorder
import com.example.ui.theme.ChatGptTextPrimary
import com.example.ui.theme.ChatGptTextSecondary

@Composable
fun SubjectAndLevelBar(
    selectedLevel: String,
    onLevelSelected: (String) -> Unit,
    selectedSubject: String,
    onSubjectSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val levels = listOf(
        "Escuela / Niños" to "Niños",
        "Colegio / Secundaria" to "Secundaria",
        "Universidad / Adultos" to "Universidad"
    )

    val subjects = listOf(
        "Todas" to "Todas las materias",
        "Matemáticas" to "Matemáticas",
        "Robótica" to "Robótica",
        "Computación" to "Computación",
        "Lenguaje" to "Lenguaje",
        "Historia" to "Historia",
        "Ciencias Naturales" to "Ciencias",
        "Estudios Sociales" to "Soc. y Geografía",
        "Educación Física" to "Educación Física",
        "ECA" to "ECA / Arte",
        "Animación a la Lectura" to "Lectura"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("subject_level_bar"),
        color = ChatGptBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            // Level Selector Row (ChatGPT clean gray chips)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                levels.forEach { (key, label) ->
                    val isSelected = selectedLevel == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { onLevelSelected(key) },
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = ChatGptChipBg,
                            labelColor = ChatGptTextSecondary,
                            selectedContainerColor = ChatGptTextPrimary,
                            selectedLabelColor = ChatGptBackground
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = ChatGptChipBorder,
                            selectedBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.height(30.dp).testTag("level_chip_$key")
                    )
                }
            }

            // Subject Selector Row (ChatGPT pill chips)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                subjects.forEach { (key, label) ->
                    val isSelected = selectedSubject == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSubjectSelected(key) },
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = ChatGptChipBg,
                            labelColor = ChatGptTextSecondary,
                            selectedContainerColor = ChatGptTextPrimary,
                            selectedLabelColor = ChatGptBackground
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = ChatGptChipBorder,
                            selectedBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.height(30.dp).testTag("subject_chip_$key")
                    )
                }
            }
        }
    }
}
