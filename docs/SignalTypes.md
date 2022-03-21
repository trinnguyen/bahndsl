# Signal Types

| German name                 | BahnDSL Type  | Aspects                        | Initial | stop   | go    | caution         | shunt        | 
|-----------------------------|---------------|--------------------------------|---------|--------|-------|-----------------|--------------|
| Einfahrsignal               | entry         | stop<br>go<br>caution          | stop    | red    | green | green<br>yellow | -            |
| Ausfahrsignal               | exit          | stop<br>go<br>caution<br>shunt | stop    | red    | green | green<br>yellow | red<br>white |
| Blocksignal                 | block         | stop<br>go                     | stop    | red    | green | -               | -            |
| Vorsignal                   | distant       | stop<br>go<br>caution          | stop    | yellow | green | yellow<br>green | -            |
| Sperrsignal                 | shunting      | stop<br>shunt                  | stop    | red    | -     | -               | white        |
| Sperrsignal                 | halt          | stop                           | red     | red    | -     | -               | -            |

| BahnDSL Type  | Aspects     | Initial |
|---------------|-------------|---------|
| platformlight | high<br>low | high    |
| peripheral    | high<br>low | high    |


