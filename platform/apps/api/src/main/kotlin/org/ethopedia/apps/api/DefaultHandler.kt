package org.ethopedia.apps.api

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

class DefaultHandler : RequestHandler<String, String> {
    override fun handleRequest(input: String, context: Context): String {
        return "hi, there! 👋"
    }
}
