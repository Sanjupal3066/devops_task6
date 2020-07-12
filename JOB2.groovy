status=$(sudo cat /task6/content.txt)
if [[ "$status" == "html" ]]
then
    if sudo kubectl get all | grep httpd-deploy
    then
        POD=$(sudo kubectl get pod -l type=http -o jsonpath="{.items[0].metadata.name}")
        sudo kubectl cp /task6/Front.html $POD:/usr/local/apache2/htdocs/
    else
        sudo kubectl create -f /task6/httpd.yml
        POD=$(sudo kubectl get pod -l type=http -o jsonpath="{.items[0].metadata.name}")
        sleep 30
        sudo kubectl cp /task6/Front.html $POD:/usr/local/apache2/htdocs/
    fi
elif [[ "$status" == "php" ]]
then
    if sudo kubectl get all | grep php-deploy
    then
        POD=$(sudo kubectl get pod -l type=php -o jsonpath="{.items[0].metadata.name}")
        sudo kubectl cp /task6/Front.php $POD:/var/www/html/
    else
        sudo kubectl create -f /task6/php.yml
        POD=$(sudo kubectl get pod -l type=php -o jsonpath="{.items[0].metadata.name}")
        sleep 30
        sudo kubectl cp /task6/Front.php $POD:/var/www/html
    fi
else
    echo "No Server Found"
    exit 1
fi
