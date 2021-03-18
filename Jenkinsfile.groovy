properties([
    buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '10', numToKeepStr: '10')), 
    disableConcurrentBuilds(), 
    parameters([
    choice(choices: ['a', 'd'], description: 'Apply_Delete', name: 'ACTION'), 
    choice(choices: ['dev', 'qa', 'stage', 'prod'], description: 'Which Environment? ', name: 'ENVIRONMENT')]), 
    ])

node {
	stage("Pull Repo"){
		checkout([$class: 'GitSCM', branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/turanmehmet/infrastructure.git']]])
}
stage("Format"){
		timestamps {
            ws("workspace/infrastructura/vpc"){
                sh "make f"
            }
    }
}
	stage("Initialize"){
		timestamps {
            ws("workspace/infrastructura/vpc"){
                sh "make i"
            }
    }
}
	stage("Plan"){
		timestamps {
            ws("workspace/infrastructura/vpc"){
                sh "make p"
            }
        }
}
    stage("Apply"){
            timestamps {
                ws("workspace/infrastructura/vpc"){
                    sh "make ${ACTION}"
                }
            }
    }
    stage("Clean Up"){
            timestamps {
                ws("workspace/infrastructura/vpc"){
                    sh "make c"
                }
            }
    }
	stage("Send Notifications to Slack"){
		slackSend color: '#BADA55', message: 'Hello, World!'
	}
	stage("Send Email to Support"){
		mail bcc: '', body: 'Running', cc: 'support@company.com', from: '', replyTo: '', subject: 'Test', to: 'mehmet.72.turan@gmail.com'
	}
    stage("Sleep"){
		sleep 60000000
    }
    stage("Intentionally Failed"){
    		error 'failed'
    }
    	stage("Call Another Job"){
    		build "Packer"
    }
    stage("Script"){
		sh label: '', script: 
		'''#!/bin/bash
			if [ ! -d /tmp/foo.txt ]; 
			then
				echo "Folder not found!"
				echo "Creating a folder"
				mkdir -p "/tmp/foo.txt" 
			fi
		'''
	}
}
