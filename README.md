<h2>Приложение по переводу денежных средств.</h2>
<h3>Описание функционала</h3>
<h4>Приложение реализовано с помощью технолии Akka(Core, Streams) и содержит следующие акторы:</h4>
<ul>
    <li>PaymentsReader</li>
    <li>PaymentChecker</li>
    <li>LogIncorrectPayment</li>
    <li>PaymentParticipant</li>
    <li>Participant</li>
</ul> 
<h4>Актор PaymentsReader считывает из указанного в конфигурации каталога файлы по маске имени, также указанной в конфигурации</h4>
<h4>Актор PaymentChecker принимает сообщение от актора PaymentsReader по каждой строке платежа, и, в случае не удовлетворения структуре, откидывает
сообщения в акток LogIncorrect Payment, а в случае успеха создает по одному актору на каждого из участников платежа и отправляет им сообщение.<h4>
<h4>Актор LogIncorrectPayment принимает сообщение с текстом ошибки некорректного перевода и пишет в лог.</h4>
<h4>Актор PaymentParticipant создается с именем переданным в сообщении и значением balance, полученным из конфигурации, и принимает сообщение
Payment в котором определяется знак платежа, производится действие с балансом и значением value, логируется результат. Если при списании баланса
не хватает средств, происходит логирование нехватки баланса, он остается прежним, а актор отсылает сообщение StopPayment на актор Participant.
В случае если на актор пришло такое сообщение, нужно "отменить" платеж и залогировать действие.</h4>
