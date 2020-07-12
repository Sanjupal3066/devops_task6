job("JOB1_GITHUB")
{
	description("In this Job I clone repo from GitHub")
	scm {
		github("Sanjupal3066/devops_task6","master")
	}
	triggers {
		scm("* * * * *")
	}
	steps{
		shell("""
		sudo mkdir -p /task6
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
		shell("sh /task6/JOB2.sh")
	}
}

job("JOB3_Testing_and_Mailing")
{
	description("In this Job I do testing of my webpage and if there is any mistake then send mail to developer")
	triggers {
		upstream("JOB2_Launching_WebServer","SUCCESS")
	}
	steps{
		shell("sh /task6/JOB3.sh")
	}
	publishers {
        mailer('spal3066@gmail.com', false, false)
    }
}
