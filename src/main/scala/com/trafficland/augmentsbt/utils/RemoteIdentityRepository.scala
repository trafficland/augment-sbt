package com.trafficland.augmentsbt.utils

import java.util

import scala.collection.convert.decorateAsJava.asJavaCollectionConverter
import com.jcraft.jsch.agentproxy.{AgentProxy, Buffer, Connector}
import com.jcraft.jsch.{Identity, IdentityRepository}

class RemoteIdentityRepository(connector: Connector) extends IdentityRepository {
  private val agent = new AgentProxy(connector)

  def getIdentities: java.util.Vector[Identity] = {
    val identities = agent.getIdentities.toIterable
    val decryptedIdentities =
      identities.map { identity =>
        new Identity {
          private val blob = identity.getBlob
          private val algName = new String(new Buffer(blob).getString)

          override def getSignature(data: Array[Byte]): Array[Byte] = agent.sign(blob, data)

          override def decrypt(): Boolean = true

          override def getName: String = new String(identity.getComment)

          override def setPassphrase(passphrase: Array[Byte]) = true

          override def clear(): Unit = {}

          override def getPublicKeyBlob: Array[Byte] = blob

          override def getAlgName: String = algName

          override def isEncrypted = false
        }
      }
    new util.Vector[Identity](decryptedIdentities.asJavaCollection)
  }

  def add(identity: Array[Byte]): Boolean = agent.addIdentity(identity)

  def remove(blob: Array[Byte]): Boolean = agent.removeIdentity(blob)

  def removeAll(): Unit = {
    agent.removeAllIdentities()
  }

  def getName: String = agent.getConnector.getName

  def getStatus: Int =
    if (agent.getConnector.isAvailable) IdentityRepository.RUNNING
    else IdentityRepository.NOTRUNNING
}
