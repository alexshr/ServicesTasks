# ServicesTasks
Курс OTUS. Домашнее задание по теме "Services"

Задание:

1.Создать задачу уведомления пользователя о низком заряде батареи.

2.*Создать задачу уведомления пользователя о его геолокации раз в минуту с возможностью отмены задачи из уведомления.

-----

Особенности реализации:

Сервисы выполнены в виде 2-х независимых activity

1. Мониторинг заряда батареи.
Google категорически не рекомендует опрашивать батарею напрямую, поэтому работаю через receiver. C помощью Foreground service идет непрерывный мониторинг состояния батареи. В зависимости от пользовательской настройки порогового уровня выдается уведомления двух видов (и приоритетов) для низкого и обычного уровня заряда. Для этого введены 2 канала уведомлений (иначе не получается менять их приоритет в последнем андроиде)

2. Location
Foreground service выдает уведомление о координатах и адрессе.
При запуске сервиса проводятся положенные стандартные проверки на permissions, наличие google services и  проверка location service.
При настройке регулярного location update учтена также возможность использования location из других приложений (кэширование) и задан expiration duration для гарантированного отключения location update (бережем батарею)

При нажатии на уведомления идет переход на соответствующий activity. Кроме того в уведомлениях присутствует action cancel для прямой остановки сервиса.

Протестировано на эмуляторе api 28
