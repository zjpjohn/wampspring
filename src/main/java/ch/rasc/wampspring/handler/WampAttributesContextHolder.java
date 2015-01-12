/**
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.rasc.wampspring.handler;

import org.springframework.core.NamedThreadLocal;

import ch.rasc.wampspring.message.WampMessage;

/**
 * @author Rossen Stoyanchev
 * @author Ralph Schaer
 */
public abstract class WampAttributesContextHolder {

	private static final ThreadLocal<WampAttributes> attributesHolder = new NamedThreadLocal<>(
			"WAMP session attributes");

	/**
	 * Reset the WampAttributes for the current thread.
	 */
	public static void resetAttributes() {
		attributesHolder.remove();
	}

	/**
	 * Bind the given WampAttributes to the current thread,
	 * @param attributes the RequestAttributes to expose
	 */
	public static void setAttributes(WampAttributes attributes) {
		if (attributes != null) {
			attributesHolder.set(attributes);
		}
		else {
			resetAttributes();
		}
	}

	/**
	 * Extract the WAMP session attributes from the given message, wrap them in a
	 * {@link WampAttributes} instance and bind it to the current thread,
	 * @param message the message to extract session attributes from
	 */
	public static void setAttributesFromMessage(WampMessage message) {
		setAttributes(WampAttributes.fromMessage(message));
	}

	/**
	 * Return the WampAttributes currently bound to the thread.
	 * @return the attributes or {@code null} if not bound
	 */
	public static WampAttributes getAttributes() {
		return attributesHolder.get();
	}

	/**
	 * Return the WampAttributes currently bound to the thread or raise an
	 * {@link java.lang.IllegalStateException} if none are bound..
	 * @return the attributes, never {@code null}
	 * @throws java.lang.IllegalStateException if attributes are not bound
	 */
	public static WampAttributes currentAttributes() throws IllegalStateException {
		WampAttributes attributes = getAttributes();
		if (attributes == null) {
			throw new IllegalStateException(
					"No thread-bound WampAttributes found. "
							+ "Your code is probably not processing a client message and executing in "
							+ "message-handling methods invoked by the SimpAnnotationMethodMessageHandler?");
		}
		return attributes;
	}

}