package com.eaut.footballclubmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.eaut.footballclubmanagement.models.Player;
import com.eaut.footballclubmanagement.utils.PlayerInputValidator;
import com.eaut.footballclubmanagement.utils.PlayerIntent;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Map;
import java.util.Random;

public class PlayerFormActivity extends AppCompatActivity {
    private TextInputEditText etPlayerName;
    private TextInputEditText etJerseyNumber;
    private TextInputEditText etClub;
    private TextInputEditText etPac;
    private TextInputEditText etSho;
    private TextInputEditText etPas;
    private TextInputEditText etDri;
    private TextInputEditText etDef;
    private TextInputEditText etPhy;
    private TextInputEditText etMatches;
    private TextInputEditText etGoals;
    private TextInputEditText etAssists;
    private TextInputEditText etMvp;
    private AutoCompleteTextView etPosition;
    private TextInputLayout tilPlayerName;
    private TextInputLayout tilJerseyNumber;
    private TextInputLayout tilPosition;
    private TextInputLayout tilClub;
    private Button btnSavePlayer;
    private PlayerViewModel playerViewModel;
    private Player originalPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_form);
        bindViews();

        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        originalPlayer = PlayerIntent.readPlayer(getIntent());

        String[] positions = getResources().getStringArray(R.array.player_positions);
        etPosition.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, positions));
        etPosition.setOnClickListener(v -> etPosition.showDropDown());

        if (originalPlayer == null) {
            prefillDefaults();
        } else {
            prefillPlayer(originalPlayer);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnRandomStats).setOnClickListener(v -> randomStats());
        btnSavePlayer.setOnClickListener(v -> savePlayer());
    }

    private void bindViews() {
        etPlayerName = findViewById(R.id.etPlayerName);
        etJerseyNumber = findViewById(R.id.etJerseyNumber);
        etPosition = findViewById(R.id.etPosition);
        etClub = findViewById(R.id.etClub);
        etMatches = findViewById(R.id.etMatches);
        etGoals = findViewById(R.id.etGoals);
        etAssists = findViewById(R.id.etAssists);
        etMvp = findViewById(R.id.etMvp);
        etPac = findViewById(R.id.etPac);
        etSho = findViewById(R.id.etSho);
        etPas = findViewById(R.id.etPas);
        etDri = findViewById(R.id.etDri);
        etDef = findViewById(R.id.etDef);
        etPhy = findViewById(R.id.etPhy);
        tilPlayerName = findViewById(R.id.tilPlayerName);
        tilJerseyNumber = findViewById(R.id.tilJerseyNumber);
        tilPosition = findViewById(R.id.tilPosition);
        tilClub = findViewById(R.id.tilClub);
        btnSavePlayer = findViewById(R.id.btnSavePlayer);
    }

    private void prefillDefaults() {
        etClub.setText(R.string.free_agent);
        setNumber(etMatches, 0);
        setNumber(etGoals, 0);
        setNumber(etAssists, 0);
        setNumber(etMvp, 0);
        setNumber(etPac, 70);
        setNumber(etSho, 70);
        setNumber(etPas, 70);
        setNumber(etDri, 70);
        setNumber(etDef, 70);
        setNumber(etPhy, 70);
    }

    private void prefillPlayer(Player player) {
        TextView title = findViewById(R.id.tvFormTitle);
        title.setText(R.string.edit_player_title);
        etPlayerName.setText(player.getFullName());
        setNumber(etJerseyNumber, player.getJerseyNumber());
        etPosition.setText(positionLabel(player.getPosition()), false);
        etClub.setText(player.getClub());
        setNumber(etMatches, player.getMatches());
        setNumber(etGoals, player.getGoals());
        setNumber(etAssists, player.getAssists());
        setNumber(etMvp, player.getMvp());
        setNumber(etPac, player.getPac());
        setNumber(etSho, player.getSho());
        setNumber(etPas, player.getPas());
        setNumber(etDri, player.getDri());
        setNumber(etDef, player.getDef());
        setNumber(etPhy, player.getPhy());
    }

    private String positionLabel(String position) {
        if (position == null) return "";
        switch (position) {
            case "FW": return getString(R.string.position_fw);
            case "MF": return getString(R.string.position_mf);
            case "DF": return getString(R.string.position_df);
            case "GK": return getString(R.string.position_gk);
            default: return "";
        }
    }

    private void randomStats() {
        String position = textOf(etPosition);
        Random random = new Random();
        if (position.startsWith("FW")) {
            setStats(random, 75, 80, 60, 75, 30, 65);
        } else if (position.startsWith("MF")) {
            setStats(random, 70, 70, 80, 75, 65, 70);
        } else if (position.startsWith("DF")) {
            setStats(random, 65, 40, 60, 60, 80, 80);
        } else if (position.startsWith("GK")) {
            setStats(random, 75, 70, 60, 78, 45, 75);
        } else {
            Toast.makeText(this, R.string.choose_position_first, Toast.LENGTH_SHORT).show();
        }
    }

    private void setStats(Random random, int pac, int sho, int pas, int dri, int def, int phy) {
        setNumber(etPac, pac + random.nextInt(15));
        setNumber(etSho, sho + random.nextInt(15));
        setNumber(etPas, pas + random.nextInt(15));
        setNumber(etDri, dri + random.nextInt(15));
        setNumber(etDef, def + random.nextInt(15));
        setNumber(etPhy, phy + random.nextInt(15));
    }

    private void savePlayer() {
        clearErrors();
        int id = originalPlayer == null ? 0 : originalPlayer.getId();
        String health = originalPlayer == null ? "Fit" : originalPlayer.getHealthStatus();
        PlayerInputValidator.ValidationResult validation = PlayerInputValidator.validate(
                id, health, textOf(etPlayerName), textOf(etJerseyNumber), textOf(etPosition),
                textOf(etClub), textOf(etMatches), textOf(etGoals), textOf(etAssists),
                textOf(etMvp), textOf(etPac), textOf(etSho), textOf(etPas), textOf(etDri),
                textOf(etDef), textOf(etPhy));
        if (!validation.isValid()) {
            showValidationErrors(validation.getErrors());
            Toast.makeText(this, R.string.fix_form_errors, Toast.LENGTH_SHORT).show();
            return;
        }

        setSaving(true);
        Player player = validation.getPlayer();
        if (originalPlayer == null) {
            playerViewModel.addPlayer(player).observe(this, this::handleSaveResult);
        } else {
            playerViewModel.updatePlayer(player.getId(), player).observe(this, this::handleSaveResult);
        }
    }

    private void handleSaveResult(OperationResult<Player> result) {
        if (result == null) return;
        setSaving(false);
        if (result.getStatus() == OperationResult.Status.AUTH_REQUIRED) {
            redirectToLogin();
        } else if (result.isSuccess()) {
            Intent resultIntent = new Intent();
            PlayerIntent.putPlayer(resultIntent, result.getData());
            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this,
                    originalPlayer == null ? R.string.player_added : R.string.player_updated,
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showValidationErrors(Map<PlayerInputValidator.Field, String> errors) {
        for (Map.Entry<PlayerInputValidator.Field, String> error : errors.entrySet()) {
            switch (error.getKey()) {
                case NAME: tilPlayerName.setError(error.getValue()); break;
                case JERSEY: tilJerseyNumber.setError(error.getValue()); break;
                case POSITION: tilPosition.setError(error.getValue()); break;
                case CLUB: tilClub.setError(error.getValue()); break;
                case MATCHES: etMatches.setError(error.getValue()); break;
                case GOALS: etGoals.setError(error.getValue()); break;
                case ASSISTS: etAssists.setError(error.getValue()); break;
                case MVP: etMvp.setError(error.getValue()); break;
                case PAC: etPac.setError(error.getValue()); break;
                case SHO: etSho.setError(error.getValue()); break;
                case PAS: etPas.setError(error.getValue()); break;
                case DRI: etDri.setError(error.getValue()); break;
                case DEF: etDef.setError(error.getValue()); break;
                case PHY: etPhy.setError(error.getValue()); break;
            }
        }
    }

    private void clearErrors() {
        tilPlayerName.setError(null);
        tilJerseyNumber.setError(null);
        tilPosition.setError(null);
        tilClub.setError(null);
        TextInputEditText[] numericFields = {etMatches, etGoals, etAssists, etMvp,
                etPac, etSho, etPas, etDri, etDef, etPhy};
        for (TextInputEditText field : numericFields) field.setError(null);
    }

    private void setSaving(boolean saving) {
        btnSavePlayer.setEnabled(!saving);
        btnSavePlayer.setText(saving ? R.string.saving_player : R.string.save_player);
    }

    private void redirectToLogin() {
        Toast.makeText(this, R.string.session_expired, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private static String textOf(TextView view) {
        return view.getText() == null ? "" : view.getText().toString().trim();
    }

    private static void setNumber(TextView view, int value) {
        view.setText(String.valueOf(value));
    }
}
