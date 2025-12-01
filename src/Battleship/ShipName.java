package Battleship;

public class ShipName {
    String shipName;
    ShipShape shipShape;
    String powerType;
    int cooldown;
    int sonarAmount;
    int[][] attackCoordinates;
    int[][] revealCoordinates;

    ShipName(String shipName, ShipShape shipShape, String powerType, int cooldown) {
        this.shipName = shipName;
        this.shipShape = new ShipShape(shipShape.shipShape, shipShape.shipCoordinates, shipShape.shipRotationAmount);
        this.powerType = powerType;
        this.cooldown = cooldown;
    }

    ShipName(String shipName, ShipShape shipShape, String powerType, int cooldown, int sonarAmount) {
        this.shipName = shipName;
        this.shipShape = new ShipShape(shipShape.shipShape, shipShape.shipCoordinates, shipShape.shipRotationAmount);
        this.powerType = powerType;
        this.cooldown = cooldown;
        this.sonarAmount = sonarAmount;
    }

    ShipName(String shipName, ShipShape shipShape, String powerType, int cooldown, int[][] attackCoordinates) {
        this.shipName = shipName;
        this.shipShape = new ShipShape(shipShape.shipShape, shipShape.shipCoordinates, shipShape.shipRotationAmount);
        this.powerType = powerType;
        this.cooldown = cooldown;
        this.attackCoordinates = attackCoordinates;
    }

    ShipName(String shipName, ShipShape shipShape, String powerType, int cooldown, int[][] attackCoordinates, int[][] revealCoordinates) {
        this.shipName = shipName;
        this.shipShape = new ShipShape(shipShape.shipShape, shipShape.shipCoordinates, shipShape.shipRotationAmount);
        this.powerType = powerType;
        this.cooldown = cooldown;
        this.attackCoordinates = attackCoordinates;
        this.revealCoordinates = revealCoordinates;
    }

    public static final ShipName[] availableNames = {
        new ShipName("Catamaran", ShipShape.availableShips[9], "SelectiveHits", 8),
        new ShipName("Coracle", ShipShape.availableShips[0], "OneShotHits", 6),
        new ShipName("Crafty Crab", ShipShape.availableShips[14], "PassiveReveal", -1), // Passive (When alive reveals a random unknown tile if alive every shot you take from other ships)
        new ShipName("Crustacean", ShipShape.availableShips[7], "Reveal&AttackRevealed in X shape", 4),
        new ShipName("Dolphin", ShipShape.availableShips[10], "SonarByAmountOfShipsBeside", 4),
        new ShipName("Electric Eels", ShipShape.availableShips[8], "FullRevealTargetShip", 3),
        new ShipName("Kayak", ShipShape.availableShips[1], "Attack1x2", 2),
        new ShipName("Longboat", ShipShape.availableShips[3], "Attack5, until miss or destroy", 6),
        new ShipName("Octopus", ShipShape.availableShips[5], "2RandomSonar", 3),
        new ShipName("Raft", ShipShape.availableShips[5], "Attack2x2", 5),
        new ShipName("Sailboat", ShipShape.availableShips[2], "Attack1x3", 4),
        new ShipName("Sea Monster", ShipShape.availableShips[12], "4RandomHits", 2),
        new ShipName("See Slug", ShipShape.availableShips[15], "5Reveal, until revealed something", 3),
        new ShipName("Killer Squid", ShipShape.availableShips[10], "?AttackReveal, RevealAdjacent", 2),
        new ShipName("Long Sub", ShipShape.availableShips[3], "1Sonar, RevealItself", 1),
        new ShipName("Mini Sub", ShipShape.availableShips[1], "1Sonar", 2),
        new ShipName("Sea Turtle", ShipShape.availableShips[13], "SuccessAttack, RevealAdjacent, ReduceCooldown1", 2), // Reveal 4 around the hit
        new ShipName("Whale", ShipShape.availableShips[6], "1Attack, RevealAdjacent", 4),
        new ShipName("Blunderbuster", ShipShape.availableShips[5], "4RandomAttackin3x3, CooldownBelow0 then 5Attack", 5),
        new ShipName("Conniving Cannoneer", ShipShape.availableShips[6], "Attack+Shape, 1AttackItself", 5),
        new ShipName("Cartographer", ShipShape.availableShips[5], "ChooseRevealTile, Revealextend1Tile", 5),
        new ShipName("Chaos Slug", ShipShape.availableShips[11], "3RandomReveal, 1RevealOnMyBoard", 1),
        new ShipName("Chaos Sub", ShipShape.availableShips[4], "Sonar, UseIncreaseCoolDown1", 1),
        new ShipName("Chaotic Machinist", ShipShape.availableShips[6], "5Attack, EachHit,Hititself1", 3),
        new ShipName("Medium Galleon", ShipShape.availableShips[2], "2Attack", 3),
        new ShipName("Magic Mirror", ShipShape.availableShips[5], "PlayLastAbilityOfOpponent",4),
        new ShipName("Mutineers", ShipShape.availableShips[2], "2Attack, EachHit,Reveal1itself", 2),
        new ShipName("Pirate Thief", ShipShape.availableShips[2], "1Attack, HitIncreaseOpponentCooldown1DecreaseItself1", 1),
        new ShipName("Skull Cove", ShipShape.availableShips[12], "4RandomAttack", -1), // Passive (DisabledAbility if last ship, and if fatal damage) Shoots 4 random shots every hit it gets
        new ShipName("Slug Squad", ShipShape.availableShips[2], "2RevealTile", 1),
        new ShipName("Valkyrie", ShipShape.availableShips[3], "2Attack, EachDestroyedShipBeside, Attack+1", 5),
    };
}