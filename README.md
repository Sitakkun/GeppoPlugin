# GeppoPlugin
MineCraftで多段ジャンプを実現するプラグインです。  
実行環境:1.15.2 PaperMC

# 遊び方
ジャンプした後にスニークをすると、もう一度ジャンプができます。  

# 使い方
`/geppo on <playername>`  
指定したプレイヤーに多段ジャンプの能力を付与します。

`/geppo off <playername>`  
指定したプレイヤーから多段ジャンプの能力を剥奪します。  

`/geppo mode <playername> <normal|random>`  
指定したプレイヤーのモードを変更します。  
  
`/geppo interval <playername> <1-10>`  
プレイヤーのジャンプ回数がリセットされるインターバルを指定します。(単位:秒)  

`/geppo limit <playername> <1-100>`  
インターバルの中での最大ジャンプ回数を指定します。  
  
`/geppo particle <playername> <on|off>`  
多段ジャンプする際に、炎のパーティクルを発生させるかを設定します。  

`/geppo strength <playername> <normal|rocket>`  
多段ジャンプの上方向の強さを指定します。  
normal : 普通のジャンプ  
rocket : 高く飛ぶジャンプ  

`/geppo init`  
プラグインを初期化します。  
(サーバーの再起動や、すべてのプレイヤーの情報をリセットしたい場合に使用)

# モード紹介  
## NORMAL  
普通に多段ジャンプができる。  
![Minecraft 2021 02 13 - 16 18 52 02](https://user-images.githubusercontent.com/46716880/107844890-bd5b1480-6e1a-11eb-81a8-00e252da5361.gif)  
## RANDOM
多段ジャンプの強さ、方向がランダムになる。  
![Minecraft 2021 02 13 - 16 20 08 03](https://user-images.githubusercontent.com/46716880/107844891-be8c4180-6e1a-11eb-8a93-15401901ca39.gif)  

