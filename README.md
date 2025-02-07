# Message Flow Processor
Process:

->Store scenarios in MySQL db with scenario, country, instance, entry_process, transform_process, exit_process, xslt_content data.

->Send XML data to Apache Active Message Queue. The queue will be in the format of scenario.country.instance.in (eg:sc1.US.1.in).

->Inbound Adapter listens to the input queue and based on the name of the queue, it will fetch the corresponding process flow from cache or database.

->Stores the scenario, country, instance to headers. Also, stores entry process, transform process and exit process if they are not null.

->Based on the next hop, we will send the payload to next queue. If entry process exists for particular scenario, entry adapter will consume payload from entry.in.

->If transform process exists for particular scenario, transform adapter will consume payload from transform.in and will transform the XML data to JSON based on the XSLT content. Then, it sends the payload to next queue.

->If exit process exists, exit adapter will consume payload from exit.in and will send the payload to outbound.in

->Outbound Adapter consumes data from outbound.in queue and sends payload to output queue which is in the format of scenario.country.instance.out (eg:sc1.US.1.out)


Steps:

-> To run Active MQ: docker run -d --name activemq -p 8161:8161 -p 61616:61616 rmohr/activemq

-> To access Active MQ: http://localhost:8161/admin username:admin & password:admin

-> Clone the repository git clone https://github.com/Jayasri1999/messageFlowProcessor.git

-> Build the Project mvn clean install

-> Run the application mvn spring-boot:run

-> Run the following command for Windows:

$creds = Get-Credential -Credential admin
Invoke-WebRequest -Uri "http://localhost:8161/api/message/sc1.US.1.in?type=queue"  -Method Post  -Body '<order><id>12345</id><customer>Jayasri</customer><amount>250.00</amount></order>'  -ContentType "text/xml"  -Credential $creds

-> We can observe the results in ActiveMQ web console under "Queues" tab.
