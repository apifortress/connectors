import com.apipulse.afconnector.AFConnectorWrapper
import groovyx.net.http.*
import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.JSON
/**
 * The Hipchat connector.
 * Params required:
 * roomId
 * token
 * @author Simone Pezzano - simone@apifortress.com
 *
 */
public class SlackWebhooks extends AFConnectorWrapper{

	private static String ROOTURL = 'https://api.hipchat.com/v2/room/'

	public void run(){
		/*
		 * The text that is going to be displayed
		 */
		final String text = 'error detected while testing '+message.projectName+' / '+message.test.name+'. Review the event at https://mastiff.apifortress.com/app/web/events/details/'+message.eventId+'?cid='+message.companyId;
		if(message.criticalFailures.size()>0)
			text = 'critical* '+text
		text = 'APIFORTRESS : '+text
		/*
		 * The username
		 */
		final String username = params['username'] ?: 'ApiFortress'

		final String messageFormat = 'text'
		/*
		 * The channel
		 */
		final String roomId = params['roomId']

		String token = params['token']

		String url = ROOTURL+roomId+'/message'
		/*
		 * The request
		 */
		final HTTPBuilder http = new HTTPBuilder(url+'?auth_token='+token)

		http.request(POST,JSON){
			body = [message:text]
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
