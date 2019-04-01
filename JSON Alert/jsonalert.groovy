import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.POST
import groovy.json.JsonOutput
import groovyx.net.http.HTTPBuilder

import com.apipulse.afconnector.AFConnectorWrapper

public class JSONAlert extends AFConnectorWrapper{

	public void run(){
		final String url = params['url']

		final String data = JsonOutput.toJson(message)

				final HTTPBuilder http = new HTTPBuilder(url)
		http.request(POST,JSON){
			body = data
			response.success = { resp ->
				println "Success! ${resp.status}"
			}
			response.failure = { resp,reader ->
				println "Request failed with status ${resp.status}"
				System.out << reader
			}
		}
	}
}
