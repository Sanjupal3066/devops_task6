status=$(sudo cat /task6/content.txt)
code=""
if [[ "$status" == "html" ]]
then
	code=$(curl -o /dev/null -s -w "%{http_code}" http://192.168.99.100:30000/Front.html)
else
	code=$(curl -o /dev/null -s -w "%{http_code}" http://192.168.99.100:31000/Front.php)
fi
echo $code
if [[ $code == 200 ]]
then
	exit 0
else
	exit 1
fi
