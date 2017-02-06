package com.trafficland.augmentsbt.utils

import com.jcraft.jsch.agentproxy.Connector
import com.jcraft.jsch.{JSch, Session}
import org.eclipse.jgit.api.{TransportCommand, TransportConfigCallback}
import org.eclipse.jgit.console.ConsoleCredentialsProvider
import org.eclipse.jgit.transport.OpenSshConfig.Host
import org.eclipse.jgit.transport.{JschConfigSessionFactory, SshTransport, Transport, TransportHttp}
import org.eclipse.jgit.util.FS

object JGitExtensions {
  implicit class TransportCommandExtensions[C <: TransportCommand[C, _]](command: C) {
    def useSshAgent(connector: Connector): C = {
      command.setTransportConfigCallback(new TransportConfigCallback {
        override def configure(transport: Transport): Unit = {
          transport match {
            case sshTransport: SshTransport =>
              sshTransport.setSshSessionFactory(new SshAgentSessionFactory(connector))

            case httpTransport: TransportHttp =>
              httpTransport.setCredentialsProvider(new ConsoleCredentialsProvider())
          }
        }
      })
    }
  }

  class SshAgentSessionFactory(connector: Connector) extends JschConfigSessionFactory {
    override def createDefaultJSch(fs: FS): JSch = {
      val jsch = super.createDefaultJSch(fs)
      val identityRepository = new RemoteIdentityRepository(connector)

      jsch.setIdentityRepository(identityRepository)
      jsch
    }
    override def configure(hc: Host, session: Session): Unit = {}
  }
}
