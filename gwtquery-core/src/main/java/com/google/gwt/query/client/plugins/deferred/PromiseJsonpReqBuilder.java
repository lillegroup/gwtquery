package com.google.gwt.query.client.plugins.deferred;

import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.query.client.js.JsObjectArray;
import com.google.gwt.query.client.js.JsRegexp;
import com.google.gwt.query.client.plugins.deferred.Deferred.DeferredPromiseImpl;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Utility class used to create promises for JsonpRequestBuilder.
 * <pre>
 *    Promise p = new PromiseJsonpReqBuilder(url, 4000);
 *    
 *    p.done(new Function() {
 *      public void f() {
 *        Properties p = arguments(0);
 *      }
 *    }).fail(new Function() {
 *      public void f() {
 *        Throwable error = arguments(0);
 *      }
 *    });
 * </pre>
 */
public class PromiseJsonpReqBuilder extends DeferredPromiseImpl {
  
  private static final JsRegexp callbackRegex = new JsRegexp("^(.+[\\?&])([^=]+)=\\?(.*)$");
  
  public PromiseJsonpReqBuilder(String url) {
    this(url, null, 0);
  }

  public PromiseJsonpReqBuilder(String url, int timeout) {
    this(url, null, timeout);
  }

  public PromiseJsonpReqBuilder(String url, String callbackParam, int timeout) {
    JsonpRequestBuilder builder = new JsonpRequestBuilder();
    if (timeout > 0) {
      builder.setTimeout(timeout);
    }
    // jQuery allows a parameter callback=? to figure out the callback parameter
    if (callbackParam == null) {
      JsObjectArray<String> tmp = callbackRegex.exec(url);
      if  (tmp.length() == 4) {
        callbackParam = tmp.get(2);
        url = tmp.get(1) + tmp.get(3);
      }
    }
    if (callbackParam != null) {
      builder.setCallbackParam(callbackParam);
    }
    send(builder, url, new AsyncCallback<Object>() {
      public void onFailure(Throwable caught) {
        dfd.reject(caught);
      }

      public void onSuccess(Object result) {
        dfd.resolve(result);
      }
    });
  }

  // Using jsni because method send in JsonpRequestBuilder is private
  private final native void send(JsonpRequestBuilder bld, String url, AsyncCallback<?> cb) /*-{
    bld.@com.google.gwt.jsonp.client.JsonpRequestBuilder::send(Ljava/lang/String;Lcom/google/gwt/user/client/rpc/AsyncCallback;Z)(url,cb,false);
  }-*/;
}