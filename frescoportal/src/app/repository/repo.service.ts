import {Headers, Http} from "@angular/http";
import {Injectable} from "@angular/core";

import {Repository} from './repository';

import 'rxjs/add/operator/toPromise';

@Injectable()
export class RepositoryService {

  constructor(private http: Http) {}

  private headers = new Headers({'Content-Type': 'application/json'});

  private frescoUrl = 'http://localhost:9092';

  create(repo: Repository): Promise<Repository> {
    const url = `${this.frescoUrl}/repository`;
    console.debug("sending request ", JSON.stringify(repo));
    return this.http.post(url, JSON.stringify(repo), {headers: this.headers})
      .toPromise().
      then(res => {
        console.debug('received response ', res);
        return res;
      })
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occured', error);
    return Promise.reject(error.message || error);
  }

}
