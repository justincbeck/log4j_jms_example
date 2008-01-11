/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.net;

import java.util.Hashtable;
import java.util.Properties;

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A simple appender that publishes events to a JMS Topic. The events are
 * serialized and transmitted as JMS message type {@link ObjectMessage}.
 * 
 * <p>
 * JMS {@link Topic topics} and {@link TopicConnectionFactory topic connection
 * factories} are administered objects that are retrieved using JNDI messaging
 * which in turn requires the retreival of a JNDI {@link Context}.
 * 
 * <p>
 * There are two common methods for retrieving a JNDI {@link Context}. If a
 * file resource named <em>jndi.properties</em> is available to the JNDI API,
 * it will use the information found therein to retrieve an initial JNDI
 * context. To obtain an initial context, your code will simply call:
 * 
 * <pre>
 * InitialContext jndiContext = new InitialContext();
 * </pre>
 * 
 * <p>
 * Calling the no-argument <code>InitialContext()</code> method will also work
 * from within Enterprise Java Beans (EJBs) because it is part of the EJB
 * contract for application servers to provide each bean an environment naming
 * context (ENC).
 * 
 * <p>
 * In the second approach, several predetermined properties are set and these
 * properties are passed to the <code>InitialContext</code> contructor to
 * connect to the naming service provider. For example, to connect to JBoss
 * naming service one would write:
 * 
 * <pre>
 * Properties env = new Properties();
 * env.put(Context.INITIAL_CONTEXT_FACTORY, &quot;org.jnp.interfaces.NamingContextFactory&quot;);
 * env.put(Context.PROVIDER_URL, &quot;jnp://hostname:1099&quot;);
 * env.put(Context.URL_PKG_PREFIXES, &quot;org.jboss.naming:org.jnp.interfaces&quot;);
 * InitialContext jndiContext = new InitialContext(env);
 * </pre>
 * 
 * where <em>hostname</em> is the host where the JBoss applicaiton server is
 * running.
 * 
 * <p>
 * To connect to the the naming service of Weblogic application server one would
 * write:
 * 
 * <pre>
 * Properties env = new Properties();
 * env.put(Context.INITIAL_CONTEXT_FACTORY, &quot;weblogic.jndi.WLInitialContextFactory&quot;);
 * env.put(Context.PROVIDER_URL, &quot;t3://localhost:7001&quot;);
 * InitialContext jndiContext = new InitialContext(env);
 * </pre>
 * 
 * <p>
 * Other JMS providers will obviously require different values.
 * 
 * The initial JNDI context can be obtained by calling the no-argument
 * <code>InitialContext()</code> method in EJBs. Only clients running in a
 * separate JVM need to be concerned about the <em>jndi.properties</em> file
 * and calling {@link InitialContext#InitialContext()} or alternatively
 * correctly setting the different properties before calling {@link
 * InitialContext#InitialContext(java.util.Hashtable)} method.
 * 
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class JMSAppender extends AppenderSkeleton
{
    String securityPrincipalName;

    String securityCredentials;

    String initialContextFactoryName;

    String urlPkgPrefixes;

    String providerURL;

    String destinationBindingName;

    static final int TOPIC = 1;

    static final int QUEUE = 2;

    int destinationType;

    String cfBindingName;

    boolean delayedActivation;

    boolean activated;

    String userName;

    String password;

    boolean locationInfo;

    TopicConnection topicConnection;

    TopicSession topicSession;

    TopicPublisher topicPublisher;

    QueueConnection queueConnection;

    QueueSession queueSession;

    QueueSender queueSender;

    public JMSAppender()
    {
    }

    /**
     * The <b>TopicConnectionFactoryBindingName</b> option takes a string
     * value. Its value will be used to lookup the appropriate
     * <code>TopicConnectionFactory</code> from the JNDI context.
     */
    public void setConnectionFactoryBindingName(String cfBindingName)
    {
        this.cfBindingName = cfBindingName;
    }

    /**
     * Returns the value of the <b>TopicConnectionFactoryBindingName</b>
     * option.
     */
    public String getTopicConnectionFactoryBindingName()
    {
        return cfBindingName;
    }

    /**
     * The <b>TopicBindingName</b> option takes a string value. Its value will
     * be used to lookup the appropriate <code>Topic</code> from the JNDI
     * context.
     */
    public void setDestinationBindingName(String destinationBindingName)
    {
        this.destinationBindingName = destinationBindingName;
    }

    /**
     * Returns the value of the <b>TopicBindingName</b> option.
     */
    public String getDestinationBindingName()
    {
        return destinationBindingName;
    }

    /**
     * The <b>DestinationType</b> option takes a string value "Queue" or
     * "Topic" (case-sensitive).
     */
    public void setDestinationType(String destinationType)
    {
        if (destinationType.equals("Topic"))
        {
            this.destinationType = TOPIC;
        }
        else

        if (destinationType.equals("Queue"))
        {
            this.destinationType = QUEUE;
        }
    }

    /**
     * The <b>DelayedActivation</b> option takes a boolean value. If
     * <code>delayedActivation</code> is <code>true</code> the appender
     * options are activated when <code>append</code> is called for the first
     * time.
     */
    public void setDelayedActivation(boolean delayedActivation)
    {
        // this.delayedActivation = Boolean.valueOf( delayedActivation
        // ).booleanValue();
        this.delayedActivation = delayedActivation;
    }

    /**
     * Returns the value of the <b>DelayedActivation</b> option.
     */
    public boolean getDelayedActivation()
    {
        return delayedActivation;
    }

    /**
     * Returns value of the <b>LocationInfo</b> property which determines
     * whether location (stack) info is sent to the remote subscriber.
     */
    public boolean getLocationInfo()
    {
        return locationInfo;
    }

    /**
     * Options are activated and become effective after calling this method when
     * <code>DelayedActivation</code> is <code>false</code>.
     */
    public void activateOptions()
    {
        if (!delayedActivation)
        {
            internalActivateOptions();
        }
    }

    /**
     * Options are activated and become effective only after calling this
     * method.
     */
    protected void internalActivateOptions()
    {
        // to prevent more than one attempt when DelayedActivation
        // is true
        if (activated)
        {
            return;
        }
        else
        {
            activated = true;
        }

        TopicConnectionFactory topicConnectionFactory;
        QueueConnectionFactory queueConnectionFactory;

        try
        {
            Context jndi;

            LogLog.debug("Getting initial context.");
            if (initialContextFactoryName != null)
            {
                Properties env = new Properties();
                env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactoryName);
                if (providerURL != null)
                {
                    env.put(Context.PROVIDER_URL, providerURL);
                }
                else
                {
                    LogLog.warn("You have set InitialContextFactoryName option but not the " + "ProviderURL. This is likely to cause problems.");
                }
                if (urlPkgPrefixes != null)
                {
                    env.put(Context.URL_PKG_PREFIXES, urlPkgPrefixes);
                }

                if (securityPrincipalName != null)
                {
                    env.put(Context.SECURITY_PRINCIPAL, securityPrincipalName);
                    if (securityCredentials != null)
                    {
                        env.put(Context.SECURITY_CREDENTIALS, securityCredentials);
                    }
                    else
                    {
                        LogLog.warn("You have set SecurityPrincipalName option but not the " + "SecurityCredentials. This is likely to cause problems.");
                    }
                }
                jndi = new InitialContext(env);
            }
            else
            {
                jndi = new InitialContext();
            }

            if (destinationType == TOPIC)
            {
                LogLog.debug("Looking up [" + cfBindingName + "]");
                topicConnectionFactory = (TopicConnectionFactory) lookup(jndi, cfBindingName);
                LogLog.debug("About to create TopicConnection.");
                if (userName != null)
                {
                    topicConnection = topicConnectionFactory.createTopicConnection(userName, password);
                }
                else
                {
                    topicConnection = topicConnectionFactory.createTopicConnection();
                }

                LogLog.debug("Creating TopicSession, non-transactional, " + "in AUTO_ACKNOWLEDGE mode.");
                topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

                LogLog.debug("Looking up topic name [" + destinationBindingName + "].");
                Topic topic = (Topic) lookup(jndi, destinationBindingName);

                LogLog.debug("Creating TopicPublisher.");
                topicPublisher = topicSession.createPublisher(topic);

                LogLog.debug("Starting TopicConnection.");
                topicConnection.start();
            }
            else

            if (destinationType == QUEUE)
            {
                LogLog.debug("Looking up [" + cfBindingName + "]");
                queueConnectionFactory = (QueueConnectionFactory) lookup(jndi, cfBindingName);
                LogLog.debug("About to create QueueConnection.");
                if (userName != null)
                {
                    queueConnection = queueConnectionFactory.createQueueConnection(userName, password);
                }
                else
                {
                    queueConnection = queueConnectionFactory.createQueueConnection();
                }

                LogLog.debug("Creating QueueSession, non-transactional, " + "in AUTO_ACKNOWLEDGE mode.");
                queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

                LogLog.debug("Looking up queue name [" + destinationBindingName + "].");
                Queue queue = (Queue) lookup(jndi, destinationBindingName);

                LogLog.debug("Creating QueueSender.");
                queueSender = queueSession.createSender(queue);

                LogLog.debug("Starting QueueConnection.");
                queueConnection.start();
            }

            jndi.close();
        }
        catch (Exception e)
        {
            errorHandler.error("Error while activating options for appender named [" + name + "].", e, ErrorCode.GENERIC_FAILURE);
        }
    }

    protected Object lookup(Context ctx, String name) throws NamingException
    {
        try
        {
            return ctx.lookup(name);
        }
        catch (NameNotFoundException e)
        {
            LogLog.error("Could not find name [" + name + "].");
            throw e;
        }
    }

    protected boolean checkEntryConditions(LoggingEvent event)
    {
        String fail = null;

        if (delayedActivation)
        {
            internalActivateOptions();
        }

        if (destinationType == TOPIC)
        {
            if (this.topicConnection == null)
            {
                fail = "No TopicConnection";
            }
            else if (this.topicSession == null)
            {
                fail = "No TopicSession";
            }
            else if (this.topicPublisher == null)
            {
                fail = "No TopicPublisher";
            }
        }
        else

        if (destinationType == QUEUE)
        {
            if (this.queueConnection == null)
            {
                fail = "No QueueConnection";
            }
            else if (this.queueSession == null)
            {
                fail = "No QueueSession";
            }
            else if (this.queueSender == null)
            {
                fail = "No QueueSender";
            }
            if (fail != null)
            {
                try
                {
                    if (topicSession != null)
                        topicSession.close();
                    if (topicConnection != null)
                        topicConnection.close();
                }
                catch (Exception e)
                {
                    LogLog.error("Error while closing JMSAppender [" + name + "].", e);
                }
            }

            try
            {
                if (destinationType == TOPIC)
                {
                    ObjectMessage msg = topicSession.createObjectMessage();
                    if (locationInfo)
                    {
                        event.getLocationInformation();
                    }
                    msg.setObject(event);
                    topicPublisher.publish(msg);
                }
                else

                if (destinationType == QUEUE)
                {
                    ObjectMessage msg = queueSession.createObjectMessage();
                    if (locationInfo)
                    {
                        event.getLocationInformation();
                    }
                    msg.setObject(event);
                    queueSender.send(msg);
                }
            }
            catch (Exception e)
            {
                errorHandler.error("Could not publish/send message in JMSAppender [" + name + "].", e, ErrorCode.GENERIC_FAILURE);
            }
        }

        if (fail != null)
        {
            errorHandler.error(fail + " for JMSAppender named [" + name + "].");
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Close this JMSAppender. Closing releases all resources used by the
     * appender. A closed appender cannot be re-opened.
     */
    public synchronized void close()
    {
        // The synchronized modifier avoids concurrent append and close
        // operations

        if (this.closed)
            return;

        LogLog.debug("Closing appender [" + name + "].");
        this.closed = true;

        try
        {
            if (topicSession != null)
                topicSession.close();
            if (topicConnection != null)
                topicConnection.close();
        }
        catch (Exception e)
        {
            LogLog.error("Error while closing JMSAppender [" + name + "].", e);
        }
        // Help garbage collection
        topicPublisher = null;
        topicSession = null;
        topicConnection = null;
    }

    /**
     * This method called by {@link AppenderSkeleton#doAppend} method to do most
     * of the real appending work.
     */
    public void append(LoggingEvent event)
    {
        if (!checkEntryConditions(event))
        {
            return;
        }

        try
        {
            ObjectMessage msg = topicSession.createObjectMessage();
            if (locationInfo)
            {
                event.getLocationInformation();
            }
            msg.setObject(event);
            topicPublisher.publish(msg);
        }
        catch (Exception e)
        {
            errorHandler.error("Could not publish message in JMSAppender [" + name + "].", e, ErrorCode.GENERIC_FAILURE);
        }
    }

    /**
     * Returns the value of the <b>InitialContextFactoryName</b> option. See
     * {@link #setInitialContextFactoryName} for more details on the meaning of
     * this option.
     */
    public String getInitialContextFactoryName()
    {
        return initialContextFactoryName;
    }

    /**
     * Setting the <b>InitialContextFactoryName</b> method will cause this
     * <code>JMSAppender</code> instance to use the {@link
     * InitialContext#InitialContext(Hashtable)} method instead of the
     * no-argument constructor. If you set this option, you should also at least
     * set the <b>ProviderURL</b> option.
     * 
     * <p>
     * See also {@link #setProviderURL(String)}.
     */
    public void setInitialContextFactoryName(String initialContextFactoryName)
    {
        this.initialContextFactoryName = initialContextFactoryName;
    }

    public String getProviderURL()
    {
        return providerURL;
    }

    public void setProviderURL(String providerURL)
    {
        this.providerURL = providerURL;
    }

    String getURLPkgPrefixes()
    {
        return urlPkgPrefixes;
    }

    public void setURLPkgPrefixes(String urlPkgPrefixes)
    {
        this.urlPkgPrefixes = urlPkgPrefixes;
    }

    public String getSecurityCredentials()
    {
        return securityCredentials;
    }

    public void setSecurityCredentials(String securityCredentials)
    {
        this.securityCredentials = securityCredentials;
    }

    public String getSecurityPrincipalName()
    {
        return securityPrincipalName;
    }

    public void setSecurityPrincipalName(String securityPrincipalName)
    {
        this.securityPrincipalName = securityPrincipalName;
    }

    public String getUserName()
    {
        return userName;
    }

    /**
     * The user name to use when {@link
     * TopicConnectionFactory#createTopicConnection(String, String) creating a
     * topic session}. If you set this option, you should also set the
     * <b>Password</b> option. See {@link #setPassword(String)}.
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    /**
     * The paswword to use when creating a topic session.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * If true, the information sent to the remote subscriber will include
     * caller's location information. By default no location information is sent
     * to the subscriber.
     */
    public void setLocationInfo(boolean locationInfo)
    {
        this.locationInfo = locationInfo;
    }

    /**
     * Returns the TopicConnection used for this appender. Only valid after
     * activateOptions() method has been invoked.
     */
    protected TopicConnection getTopicConnection()
    {
        return topicConnection;
    }

    /**
     * Returns the TopicSession used for this appender. Only valid after
     * activateOptions() method has been invoked.
     */
    protected TopicSession getTopicSession()
    {
        return topicSession;
    }

    /**
     * Returns the TopicPublisher used for this appender. Only valid after
     * activateOptions() method has been invoked.
     */
    protected TopicPublisher getTopicPublisher()
    {
        return topicPublisher;
    }

    /**
     * The JMSAppender sends serialized events and consequently does not require
     * a layout.
     */
    public boolean requiresLayout()
    {
        return false;
    }
}
