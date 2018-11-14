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
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.bcel6_2_0.verifier.exc;

/**
 * Instances of this class are thrown by BCEL's class file verifier "JustIce" when
 * a class file does not pass the verification pass 3. Note that the pass 3 used by
 * "JustIce" involves verification that is usually delayed to pass 4.
 *
 * @version $Id: CodeConstraintException.java 1806200 2017-08-25 16:33:06Z ggregory $
 */
public abstract class CodeConstraintException extends VerificationException{
    private static final long serialVersionUID = -7265388214714996640L;
    /**
     * Constructs a new CodeConstraintException with null as its error message string.
     */
    CodeConstraintException() {
        super();
    }
    /**
     * Constructs a new CodeConstraintException with the specified error message.
     */
    CodeConstraintException(final String message) {
        super(message);
    }
}
