package org.grails.datastore.gorm

import grails.gorm.tests.GormDatastoreSpec
import grails.persistence.Entity

import spock.lang.Shared

class EmbeddedAssociationSpec extends GormDatastoreSpec {

    @Shared Date now = new Date()

    void "Test persistence of embedded entities"() {
        given:
            def i = new Individual(name:"Bob", address: new Address(postCode:"30483"), bio: new Bio(birthday: new Birthday(now)))

            i.save(flush:true)
            session.clear()

        when:
            i = Individual.findByName("Bob")

        then:
            i != null
            i.name == 'Bob'
            i.address != null
            i.address.postCode == '30483'
            i.bio.birthday.date == now
    }

    void "Test criteria query by embedded entities"() {
        given:
            def i = new Individual(name:"Bob", address: new Address(postCode:"30483"), bio: new Bio(birthday: new Birthday(now)))
            def j = new Individual(name:"Doug", address: new Address(postCode:"80050"), bio: new Bio(birthday: new Birthday(now)))

            i.save(flush:true)
            j.save(flush:true)
            session.clear()

        when:
            def criteria = Individual.createCriteria()
            def results = criteria.list {
                address {
                    eq('postCode', '80050')
                }
            }

        then:
            results.size() == 1
            results[0].name == 'Doug'
    }

    @Override
    List getDomainClasses() {
        [Individual, Address]
    }
}

@Entity
class Individual {
    Long id
    String name
    Address address
    Bio bio

    static embedded = ['address', 'bio']

    static mapping = {
        name index:true
    }
}

@Entity
class Address {
    Long id
    String postCode
}

// Test embedded associations with custom types
class Bio {
    Birthday birthday
}
