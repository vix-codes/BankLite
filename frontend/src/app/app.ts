import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

interface Account {
  id: number;
  name: string;
  balance: number;
  createdAt: string;
}

interface BankTransaction {
  id: number;
  fromAccountId: number | null;
  toAccountId: number | null;
  amount: number;
  type: string;
  createdAt: string;
}

@Component({
  selector: 'app-root',
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  api = '/api';
  accounts: Account[] = [];
  transactions: BankTransaction[] = [];
  message = 'Ready';
  error = '';

  newName = '';
  openingBalance = 0;

  depositAccountId = '';
  depositAmount = 0;

  withdrawAccountId = '';
  withdrawAmount = 0;

  fromAccountId = '';
  toAccountId = '';
  transferAmount = 0;

  historyAccountId = '';

  constructor(private http: HttpClient) {
  }

  ngOnInit() {
    this.loadAccounts();
  }

  totalBalance() {
    return this.accounts.reduce((sum, account) => sum + Number(account.balance), 0);
  }

  loadAccounts() {
    this.http.get<Account[]>(this.api + '/accounts').subscribe({
      next: data => {
        this.accounts = data;
      },
      error: err => this.showError(err)
    });
  }

  createAccount() {
    const body = new HttpParams()
      .set('name', this.newName)
      .set('balance', this.openingBalance);

    this.http.post<Account>(this.api + '/accounts', body).subscribe({
      next: account => {
        this.message = 'Account created for ' + account.name;
        this.error = '';
        this.newName = '';
        this.openingBalance = 0;
        this.loadAccounts();
      },
      error: err => this.showError(err)
    });
  }

  deposit() {
    const body = new HttpParams().set('amount', this.depositAmount);

    this.http.post<Account>(this.api + '/accounts/' + this.depositAccountId + '/deposit', body).subscribe({
      next: account => {
        this.message = 'Deposit completed. New balance: ' + account.balance;
        this.error = '';
        this.depositAmount = 0;
        this.loadAccounts();
      },
      error: err => this.showError(err)
    });
  }

  withdraw() {
    const body = new HttpParams().set('amount', this.withdrawAmount);

    this.http.post<Account>(this.api + '/accounts/' + this.withdrawAccountId + '/withdraw', body).subscribe({
      next: account => {
        this.message = 'Withdrawal completed. New balance: ' + account.balance;
        this.error = '';
        this.withdrawAmount = 0;
        this.loadAccounts();
      },
      error: err => this.showError(err)
    });
  }

  transfer() {
    const body = new HttpParams()
      .set('fromAccountId', this.fromAccountId)
      .set('toAccountId', this.toAccountId)
      .set('amount', this.transferAmount);

    this.http.post(this.api + '/transfer', body).subscribe({
      next: () => {
        this.message = 'Transfer completed';
        this.error = '';
        this.transferAmount = 0;
        this.loadAccounts();
      },
      error: err => this.showError(err)
    });
  }

  getTransactions() {
    this.http.get<BankTransaction[]>(this.api + '/accounts/' + this.historyAccountId + '/transactions').subscribe({
      next: data => {
        this.transactions = data;
        this.message = 'Transaction history loaded';
        this.error = '';
      },
      error: err => this.showError(err)
    });
  }

  showError(err: any) {
    this.error = err.error?.error || 'Something went wrong';
    this.message = '';
  }
}
