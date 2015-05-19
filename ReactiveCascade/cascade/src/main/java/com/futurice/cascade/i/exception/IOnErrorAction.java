/*
The MIT License (MIT)

Copyright (c) 2015 Futurice Oy and individual contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

package com.futurice.cascade.i.exception;

import com.futurice.cascade.i.action.IBaseAction;

/**
 * A function to execute in the event of an {@link java.lang.Exception} or similar irregular termination
 * such as {@link com.futurice.cascade.i.ICancellable#cancel(String)}
 */
public interface IOnErrorAction<PHANTOM_IN> extends IBaseAction<Exception> {
    /**
     * Perform some cleanup or notification onFireAction to bring this object into a rest state after
     * irregular termination.
     *
     * @param e
     * @return <code>true</code> if the error is consumed and should not propagate further down-chain.
     * The default response is <code>false</code> indicating the error is not consumed and should continue to propagate down-chain
     * @throws Exception
     */
    boolean call(Exception e) throws Exception;
}
