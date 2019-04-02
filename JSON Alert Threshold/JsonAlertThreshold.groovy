import com.apipulse.afconnector.AFCache
import com.apipulse.afconnector.AFConnectorWrapper
import groovy.json.JsonOutput
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder

import static groovyx.net.http.Method.POST

public class StatefulJSONAlert extends AFConnectorWrapper{
	public void run(){
		final String url = params['url']
		int counter = 3
		int ttl = 600
		try {
			counter = Integer.valueOf(params['counter'])
			ttl = Integer.valueOf(params['ttl'])
		}catch(Exception e){println 'Connector misconfigured for '+String.valueOf(message.getCompanyId())}

		if(ttl>3600)
			ttl = 3600
		String testId = message.test.id
		def objValue = AFCache.instance.getAlertThreshold(testId)
		if(objValue == null) {
			objValue = []
			AFCache.instance.setAlertThreshold(testId,objValue,ttl)
		}
		message.put('value1',message.test.name)
		message.put('value2',message.projectName)
		objValue.add(message)
		if(objValue.size()>=counter){
			final String data = JsonOutput.toJson(message)

			final HTTPBuilder http = new HTTPBuilder(url)
			http.request(POST){
				body = data
				requestContentType = ContentType.JSON
				response.success = { resp,reader ->
					println "Success! ${resp.status}"
				}
				response.failure = { resp,reader ->
					println "Request failed with status ${resp.status}"
					System.out << reader
				}
			}
			objValue = []
			AFCache.instance.setAlertThreshold(testId,objValue,ttl)
		}
	}
}