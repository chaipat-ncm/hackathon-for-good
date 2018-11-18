import {of as observableOf, Observable} from 'rxjs';
import {Injectable} from '@angular/core';


let counter = 0;

@Injectable()
export class UserService {

  private users = {
    nick: {name: 'Nick Jones', picture: 'assets/images/nick.png', username: 'nick'},
    eva: {name: 'Eva Moor', picture: 'assets/images/eva.png', username: 'eva'},
    jack: {name: 'Jack Williams', picture: 'assets/images/jack.png', username: 'jack'},
    lee: {name: 'Lee Wong', picture: 'assets/images/lee.png', username: 'lee'},
    alan: {name: 'Alan Thompson', picture: 'assets/images/alan.png', username: 'alan'},
    kate: {name: 'Kate Martinez', picture: 'assets/images/kate.png', username: 'kate'},
    adriel: {name: 'Adriel Walter', picture: 'assets/images/admin.png', username: 'adriel'},
    admin: {name: 'Admin', picture: 'assets/images/admin.png', username: 'admin'},
  };

  private userArray: any[];

  constructor() {
    // this.userArray = Object.values(this.users);
  }

  getUsers(): Observable<any> {
    return observableOf(this.users);
  }

  getUserArray(): Observable<any[]> {
    return observableOf(this.userArray);
  }

  getUser(): Observable<any> {
    counter = (counter + 1) % this.userArray.length;
    return observableOf(this.userArray[counter]);
  }
}
