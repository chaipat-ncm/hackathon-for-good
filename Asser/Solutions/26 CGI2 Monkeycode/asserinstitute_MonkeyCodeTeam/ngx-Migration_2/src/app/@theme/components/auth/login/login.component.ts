/**
 * @license
 * Copyright Akveo. All Rights Reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */
import {Component, Inject} from '@angular/core';
import {Router} from '@angular/router';
import {NB_AUTH_OPTIONS, NbAuthResult, NbAuthService, NbAuthSocialLink} from '@nebular/auth';
import {getDeepFromObject} from '@nebular/auth/helpers';


@Component({
  selector: 'ngx-login',
  styleUrls: ['./login.component.scss'],
  templateUrl: 'login.component.html',
})
export class NbLoginComponent {

  redirectDelay: number = 0;
  showMessages: any = {};
  strategy: string = '';

  errors: string[] = [];
  messages: string[] = [];
  user: any = {};
  submitted: boolean = false;
  socialLinks: NbAuthSocialLink[] = [];

  constructor(protected service: NbAuthService,
              @Inject(NB_AUTH_OPTIONS) protected options = {},
              protected router: Router) {

    this.redirectDelay = this.getConfigValue('forms.login.redirectDelay');
    this.showMessages = this.getConfigValue('forms.login.showMessages');
    this.strategy = this.getConfigValue('forms.login.strategy');
    this.socialLinks = this.getConfigValue('forms.login.socialLinks');
  }

  public emailErrorMsg(username): string {
    const usernameErrorMsg = 'Username is required!';
    const usernameErrorMsgInvalid = 'Username does not match with the requers!';

    if (username.invalid && username.touched && username.errors.required) {
      return usernameErrorMsg;
    } else if (username.invalid && username.touched && username.errors.pattern) {
      return usernameErrorMsgInvalid;
    }
  }

  login(): void {
    this.errors = this.messages = [];
    this.submitted = true;

    this.service.authenticate(this.strategy, this.user).subscribe((result: NbAuthResult) => {
      this.submitted = false;

      if (result.isSuccess()) {
        localStorage.setItem('currentUser', JSON.stringify({name: this.user.username}));
        this.messages = result.getMessages();
      } else {
        this.errors = result.getErrors();
      }

      // const redirect = result.getRedirect();
      const redirect = 'pages';
      if (redirect) {
        setTimeout(() => {
          return this.router.navigateByUrl(redirect);
        }, this.redirectDelay);
      }
    });
  }

  getConfigValue(key: string): any {
    return getDeepFromObject(this.options, key, null);
  }
}
