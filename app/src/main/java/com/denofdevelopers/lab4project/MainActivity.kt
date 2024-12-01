package com.denofdevelopers.lab4project

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denofdevelopers.lab4project.data.CardData
import com.denofdevelopers.lab4project.ui.theme.Lab4ProjectTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab4ProjectTheme {
                MemoryGameScreen()
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryGameScreen() {
    var cards by remember {
        mutableStateOf(
            listOf(
                Pair(Color.Red, Color.Red),
                Pair(Color.Blue, Color.Blue),
                Pair(Color.Green, Color.Green),
                Pair(Color.Yellow, Color.Yellow),
                Pair(Color.Magenta, Color.Magenta),
                Pair(Color.Cyan, Color.Cyan)
            ).flatMap { listOf(it.first, it.second) }.shuffled()
                .map { CardData(it, false) }
        )
    }
    var selectedCards by remember { mutableStateOf<MutableList<Int>>(mutableListOf()) }
    var matches by remember { mutableIntStateOf(0) }
    var mistakes by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Memory Game") },
                actions = {
                    IconButton(onClick = {
                        cards = listOf(
                            Pair(Color.Red, Color.Red),
                            Pair(Color.Blue, Color.Blue),
                            Pair(Color.Green, Color.Green),
                            Pair(Color.Yellow, Color.Yellow),
                            Pair(Color.Magenta, Color.Magenta),
                            Pair(Color.Cyan, Color.Cyan)
                        ).flatMap { listOf(it.first, it.second) }.shuffled()
                            .map { CardData(it, false) }
                        selectedCards = mutableListOf()
                        matches = 0
                        mistakes = 0
                    }) {
                        Icon(
                            painterResource(id = R.drawable.ic_refresh),
                            contentDescription = "Reset"
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text("Matches: $matches")
            Text("Mistakes: $mistakes")
            GameGrid(cards) { cardIndex ->
                if (selectedCards.size < 2 && !cards[cardIndex].isFaceUp && !selectedCards.contains(
                        cardIndex
                    )
                ) {
                    selectedCards.add(cardIndex)
                    cards = cards.mapIndexed { index, card ->
                        if (index == cardIndex) {
                            card.copy(isFaceUp = true)
                        } else {
                            card
                        }
                    }
                    if (selectedCards.size == 2) {
                        coroutineScope.launch {
                            delay(1000)
                            val firstCard = cards[selectedCards[0]]
                            val secondCard = cards[selectedCards[1]]

                            if (firstCard.color == secondCard.color) {
                                matches++
                            } else {
                                mistakes++
                                cards = cards.mapIndexed { index, card ->
                                    if (index == selectedCards[0] || index == selectedCards[1]) {
                                        card.copy(isFaceUp = false)
                                    } else {
                                        card
                                    }
                                }
                            }
                            selectedCards.clear()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameGrid(cards: List<CardData>, onCardClick: (Int) -> Unit) {
    Column(modifier = Modifier
        .padding(8.dp)) {
        repeat(3) { row ->  // 3 rows
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                repeat(4) { column ->  // 4 columns
                    val cardIndex = row * 4 + column
                    GameCard(card = cards[cardIndex], onClick = { onCardClick(cardIndex) })
                }
            }
        }
    }
}

@Composable
fun GameCard(card: CardData, onClick: () -> Unit) {
    val screenWidth = with(LocalConfiguration.current) {
        screenWidthDp.dp
    }
    val cardSize = (screenWidth / 4) - 32.dp
    Card(
        modifier = Modifier
            .size(cardSize)
            .clickable { onClick() },
        elevation = if (card.isFaceUp) CardDefaults.cardElevation(defaultElevation = 0.dp) else CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        if (card.isFaceUp) {
            Image(
                painter = ColorPainter(card.color),
                contentDescription = "Card Color",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.card_back),
                contentDescription = "Card Back",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MemoryGameScreen()
}