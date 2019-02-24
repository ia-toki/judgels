export type Scoreboard = IcpcScoreboard | IoiScoreboard | GcjScoreboard | BundleScoreboard;

export interface ScoreboardState {
  problemJids: string[];
  problemAliases: string[];
  contestantJids: string[];
  problemPoints?: number[];
}

export interface BundleScoreboard {
  state: ScoreboardState;
  content: BundleScoreboardContent;
}

export interface BundleScoreboardContent {
  problemItems: number[];
  entries: BundleScoreboardEntry[];
}

export interface BundleScoreboardEntry {
  rank: number;
  contestantJid: string;
  answeredItems: number[];
  totalAnsweredItems: number;
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
  Frozen,
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

export interface GcjScoreboard {
  state: ScoreboardState;
  content: GcjScoreboardContent;
}

export interface GcjScoreboardContent {
  entries: GcjScoreboardEntry[];
}

export interface GcjScoreboardEntry {
  rank: number;
  contestantJid: string;
  totalPoints: number;
  totalPenalties: number;
  attemptsList: number[];
  penaltyList: number[];
  problemStateList: GcjScoreboardProblemState[];
}

export enum GcjScoreboardProblemState {
  NotAccepted = 0,
  Accepted,
  Frozen,
}
