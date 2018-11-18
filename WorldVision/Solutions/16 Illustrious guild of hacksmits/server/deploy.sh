HOST="USER@IP"

FOLDER="hackathon-for-good"
TAR="model.tar.gz"

cd ../.. && tar --exclude="$FOLDER/*.swp" -czvf $TAR $FOLDER
scp $TAR $HOST:
ssh $HOST tar xzvf $TAR

ssh $HOST "cd $FOLDER/modules/ARM && npm i --no-save"
ssh $HOST "cd $FOLDER/modules/APM && npm i --no-save"
ssh $HOST "cd $FOLDER/modules/DIM && npm i --no-save"
ssh $HOST "cd $FOLDER/modules/DPM && npm i --no-save"
ssh $HOST "cd $FOLDER/server && npm i --no-save && mg restart"
ssh $HOST rm $TAR
rm $TAR
