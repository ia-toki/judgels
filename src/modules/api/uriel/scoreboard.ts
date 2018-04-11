export type Scoreboard = IcpcScoreboard | IoiScoreboard;

export interface ScoreboardState {
  problemJids: string[];
  problemAliases: string[];
  contestantJids: string[];
}

export interface IcpcScoreboard {
  state: ScoreboardState;
  content: IcpcScoreboardContent;
}

export interface IcpcScoreboardContent {
  entries: IcpcScoreboardEntry[];
}

export interface IcpcScoreboardEntry {
  rank: number;
  contestantJid: string;
  totalAccepted: number;
  totalPenalties: number;
  attemptsList: number[];
  penaltyList: number[];
  problemStateList: IcpcScoreboardProblemState[];
}

export enum IcpcScoreboardProblemState {
  NotAccepted = 0,
  Accepted,
  FirstAccepted,
}

export interface IoiScoreboard {
  state: ScoreboardState;
  content: IoiScoreboardContent;
}

export interface IoiScoreboardContent {
  entries: IoiScoreboardEntry[];
}

export interface IoiScoreboardEntry {
  rank: number;
  contestantJid: string;
  scores: (number | null)[];
  totalScores: number;
  lastAffectingPenalty: number;
}
