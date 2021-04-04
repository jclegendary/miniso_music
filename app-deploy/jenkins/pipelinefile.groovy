#!groovy
pipeline {
  agent { node { label 'master' } }
  tools {
    nodejs 'node_12_16_3'
  }
  environment {
    PATH = '/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:./node_modules/.bin'
  }
  parameters {
    gitParameter (
      name: 'BRANCH_TAG',
      type: 'PT_BRANCH',
      sortMode:'DESCENDING_SMART',
      defaultValue: 'origin/master',
      quickFilterEnabled: true,
      description: 'GIT分支'
    )
    choice (
      name: 'server',
      choices: '106.15.249.132',
      description: '部暑服务器'
    )
  }

  stages {
    stage("下载git库代码") {
      steps {
        dir ("${env.WORKSPACE}") {
          //git branch: 'master', credentialsId:"9aa11671-aab9-47c7-a5e1-a4be146bd587", url: 'git@172.20.2.10:products/oss/itss-web.git'
          checkout([
              $class: 'GitSCM',
              branches:[
                [
                  name: "${params.BRANCH_TAG}"
                ]
              ],
              doGenerateSubmoduleConfigurations: false,
              extensions: [],
              submoduleCfg: [],
              userRemoteConfigs: [
                [
                  credentialsId: 'git hub',
                  url: 'git@github.com:jclegendary/miniso_music.git'
                ]
              ]
          ])
        }
      }
    }
    stage("node打包") {
      steps  {
        dir ("${env.WORKSPACE}") {
          sh '''
              echo $PATH
              node -v
              yarn config set registry http://registry.npm.taobao.org
              yarn 
              yarn build
            
          '''
        }
      }
    }
    stage("zip包压缩") {
      steps {
        dir ("${env.WORKSPACE}") {
          sh '''
              rm -rf deploy
              mkdir -p deploy
              cp -rf ./dist ./deploy/
              cp -rf ./app-deploy/docker/* ./deploy/
              cd ./deploy
              tar -zcf deploy.tar.gz *
              '''
        }
      }
    }
    stage("传输文件") {
      steps {
        script {
          def reg = /\(.*\)/
          String serverStr = server
          def ip = server.replaceAll(~ reg, '')
          dir ("${env.WORKSPACE}") {
            sshPublisher paramPublish: [parameterName: 'server'], publishers: [sshPublisherDesc(configName: "${server}", sshLabel: [label: "${server}"]
              , transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand:'', execTimeout: 12000000, flatten: false
              , makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '/home/docker/miniso_audio/', remoteDirectorySDF: false
              , removePrefix: 'deploy/', sourceFiles: 'deploy/deploy.tar.gz')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)]
            echo '[INFO] SEND finished...'
          }
        }
      }
    }
    stage("启动程序") {
      steps {
        script {
          def reg = /\(.*\)/
          String serverStr = server
          def ip = server.replaceAll(~ reg, '')
          dir("${env.WORKSPACE}") {
            sshPublisher paramPublish: [parameterName: 'server'], publishers: [sshPublisherDesc(configName: "${server}", sshLabel: [label: "${server}"], transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand:
              '''
                  #!/bin/bash
                  #set -o errexit
                  cd /home/docker/miniso_audio/
                  tar -xvf deploy.tar.gz
                  rm -f deploy.tar.gz
                  docker rm -f miniso_audio
                  # docker rmi miniso_audio
                  # docker build ./ -t miniso_audio
                  docker run -itd --restart=always --name miniso_audio -p 8002:80 miniso_audio
                  rm -rf /home/docker/miniso_audio/*
                ''', execTimeout: 12000000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)]
          }
        }
      }
    }
  }
  post {
    success {
      script {
        sh '''
              content="项目名称：${JOB_NAME} 打包成功了！";
              echo "${content}"
              
          '''
      }
    }
    unstable {
      script {
        sh '''
              content="项目名称：${JOB_NAME} 打包失败了！";
              echo "${content}"
          '''
      }
    }
    failure {
      script {
        sh '''
              content="项目名称：${JOB_NAME} 打包失败了！";
              echo "${content}"
              
          '''
      }
    }
  }
}
