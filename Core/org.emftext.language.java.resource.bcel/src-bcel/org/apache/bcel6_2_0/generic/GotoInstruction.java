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
package org.apache.bcel6_2_0.generic;

/**
 * Super class for GOTO
 *
 * @version $Id: GotoInstruction.java 1812166 2017-10-13 23:48:11Z ggregory $
 */
public abstract class GotoInstruction extends BranchInstruction implements UnconditionalBranch {

    GotoInstruction(final short opcode, final InstructionHandle target) {
        super(opcode, target);
    }


    /**
     * Empty constructor needed for Instruction.readInstruction.
     * Not to be used otherwise.
     */
    GotoInstruction() {
    }
}
