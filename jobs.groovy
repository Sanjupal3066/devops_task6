job("JOB1_GITHUB")
{
	description("In this Job I clone repo from GitHub")
	scm {
		github("Sanjupal3066/mlops_day11_hw2","master")
	}
	triggers {
		scm("* * * * *")
	}
	steps{
		shell("""
		mkdir -p /task6
		sudo cp -v * /task6
		sudo rm -f *
		""")
	}
}

job("JOB2_Launching_WebServer")
{
	description("In this Job I Launch WebServer")
	triggers {
		upstream("JOB1_GITHUB","SUCCESS")
	}
	steps {
		shell("""
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
		""")
	}
}

job("JOB3_Testing_and_Mailing")
{
	description("In this Job I do testing of my webpage and if there is any mistake then send mail to developer")
	triggers {
		upstream("JOB2_Launching_WebServer","SUCCESS")
	}
	steps{
		shell("""
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
		""")
	}
	publishers {
        mailer('spal3066@gmail.com', false, false)
    }
}