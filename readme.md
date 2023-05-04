### git的基本使用
- 下载安装git，注册github账号
- 在github上创建一个新的仓库，用来存放这个项目
- 可以选择ssh连接方式，要生成秘钥（还没完全搞明白）

- git init：创建一个新的本地仓库。
- git clone：复制一个远程仓库到本地。
- git add：把一个或多个文件添加到暂存区（索引）。
- git commit：把暂存区的变化提交到本地仓库。
- git push：把本地仓库的变化推送到远程仓库。
- git pull：把远程仓库的变化拉取到本地仓库并合并。
- git status：查看你修改了哪些文件，以及哪些文件还需要添加或提交。
- git config：设置用户的配置信息，如用户名、邮箱、文件格式等。
- git branch：创建、列出或删除分支。
- git checkout：切换到另一个分支。
- git merge：合并两个分支的变化。
- git tag：给一个重要的变更打上标签，如一个版本号。
- git diff：查看两个版本之间的差异。

git init之后，用git add . 命令将.gitignore文件所指明之外的文件都添加到暂存里。

git commit -m "init repo": 把第一次的暂存区的变化提交到本地仓库，代号为init repo
