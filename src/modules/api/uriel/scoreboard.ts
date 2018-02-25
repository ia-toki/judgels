export type Scoreboard = IcpcScoreboard;

export interface IcpcScoreboard {
  state: IcpcScoreboardState;
  content: IcpcScoreboardContent;
}

export interface IcpcScoreboardState {
  problemJids: string[];
  problemAliases: string[];
  contestantJids: string[];
}

export interface IcpcScoreboardContent {
  entries: IcpcScoreboardEntry[];
}

export interface IcpcScoreboardEntry {
  rank: number;
  contestantJid: string;
  totalAccepted: number;
  totalPenalties: number;
  lastAcceptedPenalty: number;
  attemptsList: number[];
  penaltyList: number[];
  problemStateList: IcpcProblemState[];
}

export enum IcpcProblemState {
  NotAccepted = 0,
  Accepted,
  FirstAccepted,
}
